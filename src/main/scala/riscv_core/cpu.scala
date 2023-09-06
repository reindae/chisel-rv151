// See README.md for license details.

package cpu

import opcode._
import alu._
import branch_comp._
import control._
import data_out._
import imm_gen._
import IO._
import mux_alu_a._
import mux_alu_b._
import mux_csr._
import mux_dmem_addr._
import mux_dmem_data._
import mux_nop._
import mux_pcsel._
import mux_rs1._
import mux_rs2._
import mux_wbsel._
import reg_file._
import uart._

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage


/**
 * RISC-V Core top layer, connect every subparts
 */

class cpu(val CPU_CLOCK_FREQ: Int = 50000000, val BAUD_RATE: Int = 115200, val RESET_PC: Int = 1073741824) extends Module {
  val io = IO(new Bundle {
    val reset = Input(Bool())
    val bp_enable = Input(Bool())
    val serial_in = Input(UInt(1.W))
    val serial_out = Output(UInt(1.W))
  })

  val tohost_csr = RegInit(0.U(32.W))

  // CONTROL SIGNAL
  val PCSel, BrUn, BrEq, BrLt, ASel, BSel, CSRSel, CSRWEn, RegWEn, SigEx, has_rd = Wire(UInt(1.W))
  val ALUSel, DMEM_WEn, DMEM_out = Wire(UInt(4.W))
  val DMEM_in = Wire(UInt(32.W))
  val WBSel = Wire(UInt(2.W))


  /*
    Stage 1 wiring:
      pc: pc counter
      mux: selct to use pc+4 or alu output.
  */
  val s1_pc, pcsel_out = Wire(UInt(32.W))


  /* 
    Stage 2 wiring:
      pc:         pc counter
      s2_inst:    instruction for stage 2
      s2_imm:     immediate from immediate select
      rs1_val: rs1 after forwording logic （input to the alu)
      rs2_val: rs2 after forwording logic （input to the alu)
      s2_reg_rs1: rs1 before forwording logic
      s2_reg_rs2: rs2 before forwording logic
      s2_alu:     alu output.
      imem_out:   instruction from memoery
      s2_opcode:  instruction opcode
      s2_rs1:     rs1 register
      s2_rs2:     rs2 register
      s2_csr:     csr register
      read_data1: rs1 from regfile
      read_data2: rs2 from regfile
  */
  val s2_pc, s2_inst, s2_imm, rs1_val, rs2_val, s2_reg_rs1, s2_reg_rs2, s2_alu = Wire(UInt(32.W))
  val imem_out = Wire(UInt(32.W))
  val s2_rs1, s2_rs2 = Wire(UInt(5.W))
  val s2_csr = Wire(UInt(12.W))
  val rd1, rd2 = Wire(UInt(32.W))
  // DECODE -> identifying register
  s2_rs1 := s2_inst(19,15)
  s2_rs2 := s2_inst(24,20)
  s2_csr := s2_inst(31,20)


  /* 
    Stage 3 wiring:
      pc:       pc counter
      s3_inst:  instruction for stage 2
      s3_alu:   alu output
      reg_wb:    write back value
      dmem_val: value from data memory
  */
  val s3_pc, s3_inst, s3_alu, reg_wb = Wire(UInt(32.W))
  val s3_rd = Wire(UInt(5.W))
  val dmem_val = Wire(UInt(32.W))
  // DECODE
  s3_rd := s3_inst(11,7)


  // Stage 1 mux & register
  val s1_reg = RegInit(0.U(32.W))
  when (PCSel === 1.U) {
    s1_reg := s2_alu
  }
  .otherwise {
    s1_reg := s1_pc + 0x4.U(32.W)
  }
  pcsel_out := s1_reg
  

  // Stage 2 register pipelined from stage 1
  val s2_reg = RegInit(0.U(32.W))
  when (true.B) {
    s2_reg := pcsel_out
  }
  s2_pc := s2_reg


  /*
    we need previous pcsel to decide 
    whether we need to add nop instruction
    Modify: implement the branch predictor.
  */
  val prev_PCSel = RegInit(0.U(32.W))
  when (true.B) {
    prev_PCSel := PCSel
  }
  when (prev_PCSel === 1.U) {
    s2_inst := 0x13.U(32.W)
  }
  .otherwise {
    s2_inst := imem_out
  }


  /*
    TODO: implement the forwording logic for rs1 and rs2.
    check if stage 3 instruction contains rd and equal to rs1 and rs2.
  */
  when ((has_rd === 1.U) && (s3_rd === s2_rs1)) {
    rs1_val := s3_alu
  }
  .otherwise {
    rs1_val := s2_reg_rs1
  }
  when ((has_rd === 1.U) && (s3_rd === s2_rs2)) {
    rs2_val := s3_alu
  }
  .otherwise {
    rs2_val := s2_reg_rs2
  }

  when ((RegWEn === 1.U) && (s3_rd === s2_rs1)) {
    s2_reg_rs1 := reg_wb
  }
  .otherwise {
    s2_reg_rs1 := rd1
  }
  when ((RegWEn === 1.U) && (s3_rd === s2_rs2)) {
    s2_reg_rs2 := reg_wb
  }
  .otherwise {
    s2_reg_rs2 := rd2
  }


  // Instanitiate Register file
  // Asynchronous read: read data is available in the same cycle
  // Synchronous write: write takes one cycle
  val RF = Module(new reg_file())
  RF.io.we := RegWEn
  RF.io.rs1 := s2_rs1
  RF.io.rs2 := s2_rs2
  RF.io.rd1 := rd1
  RF.io.rd2 := rd2
  RF.io.wa := s3_rd
  RF.io.wd := reg_wb


  // Instanitiate CSR module
  val csr_reg = RegInit(0.U(32.W))
  when (CSRSel === 1.U) {
    csr_reg := Cat(0.U(27.W), s2_inst(19,15))
  }
  .otherwise {
    csr_reg := s2_reg_rs1
  }
  when (CSRWEn === 1.U) {
    tohost_csr := csr_reg
  }

  
  // Instanitiate Branch comparator module
  val BRCP = Module(new branch_comp())
  BRCP.io.rs1 := rs1_val
  BRCP.io.rs2 := rs2_val
  BRCP.io.BrUn := BrUn
  BRCP.io.BrEq := BrEq
  BRCP.io.BrLt := BrLt


  // Instanitiate Immediate generator module
  val IMMGEN = Module(new imm_gen())
  IMMGEN.io.inst := s2_inst
  IMMGEN.io.imm := s2_imm


  // Instanitiate ALU module
  val ALU = Module(new alu())
  ALU.io.alu_sel := ALUSel
  ALU.io.result := s2_alu
  when (ASel === 1.U) {
    ALU.io.in_A := s2_pc
  }
  .otherwise {
    ALU.io.in_A := rs1_val
  }
  when (BSel === 1.U) {
    ALU.io.in_A := s2_imm
  }
  .otherwise {
    ALU.io.in_A := rs2_val
  }


  // ONCHIP UART
  // RECEIVER
  val uart_rx_data_out = Wire(UInt(8.W))
  val uart_rx_data_out_valid, uart_rx_data_out_ready = Wire(UInt(1.W))
  // TRANSMITTER
  val uart_tx_data_in = Wire(UInt(8.W))
  val uart_tx_data_in_valid, uart_tx_data_in_ready = Wire(UInt(1.W))
  // Instanitiate UART
  val UART = Module(new uart(CPU_CLOCK_FREQ, BAUD_RATE))
  UART.reset := io.reset
  // UART RECEIVER
  UART.io.serial_in := io.serial_in
  UART.io.data_out := uart_rx_data_out
  UART.io.data_out_valid := uart_rx_data_out_valid
  UART.io.data_out_ready := uart_rx_data_out_ready
  // UART TRANSMITTER
  UART.io.serial_out := io.serial_out
  UART.io.data_in := uart_tx_data_in
  UART.io.data_in_valid := uart_tx_data_in_valid
  UART.io.data_in_ready := uart_tx_data_in_ready


  // Instruction register
  val inst_reg = RegInit(0.U(32.W))
  when (true.B) {
    inst_reg := s2_inst
  }
  s3_inst := inst_reg


  // Stage 3 register
  val s3_pc_reg = RegInit(0.U(32.W))
  when (true.B) {
    s3_pc_reg := s2_pc
  }
  s3_pc := s3_pc_reg


  // Instanitiate CONTROL LOGIC
  val CTRL = Module(new control())
  CTRL.io.curr_inst := s2_inst
  CTRL.io.prev_inst := s3_inst
  CTRL.io.BrEq := BrEq
  CTRL.io.BrLt := BrLt
  CTRL.io.curr_ALU_low2 := s2_alu(1,0)
  CTRL.io.prev_ALU_low2 := s3_alu(1,0)
  CTRL.io.PCSel := PCSel
  CTRL.io.ALUSel := ALUSel
  CTRL.io.BrUn := BrUn
  CTRL.io.ASel := ASel
  CTRL.io.BSel := BSel
  CTRL.io.CSRSel := CSRSel
  CTRL.io.CSRWEn := CSRWEn
  CTRL.io.DMEM_in := DMEM_in
  CTRL.io.DMEM_WEn := DMEM_WEn
  CTRL.io.DMEM_out := DMEM_out
  CTRL.io.sign_ext := SigEx
  CTRL.io.WBSel := WBSel
  CTRL.io.RegWEn := RegWEn
  CTRL.io.has_rd := has_rd


  // Instanitiate IO
  val cycle_p, inst_p, corr_B_p, total_B_p = RegInit(0.U(32.W))
  val IO = Module(new IO())
  IO.io.rst := io.reset
  IO.io.data_addr := s2_alu
  IO.io.s3_inst := s3_inst
  IO.io.prev_PCSel := prev_PCSel
  IO.io.cycle_p := cycle_p
  IO.io.inst_p := inst_p
  IO.io.corr_B_p := corr_B_p
  IO.io.total_B_p := total_B_p


  // Control signal for stage 3
  val alu_reg = RegInit(0.U(32.W))
  when (true.B) {
    alu_reg := s2_alu
  }
  s3_alu := alu_reg

  // TODO: implement the write back select
  when (WBSel(1)) {
    reg_wb := s3_pc + 4.U(32.W)
  }
  .otherwise {
    when (WBSel(0)) {
      reg_wb := s3_alu
    }
    .otherwise {
      reg_wb := dmem_val
    }
  }

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new cpu())
}


  // NON-ASIC-RELATED
  // BIOS Memory
  // Synchronous read: read takes one cycle
  // Synchronous write: write takes one cycle
  /*
    val bios_addra, bios_addrb = Wire(UInt(12.W))
    val bios_douta, bios_doutb = Wire(UInt(32.W))
    val bios_ena, bios_enb = Wire(UInt(1.W))
  */
  // Instanitiate BIOS MEMORY
  /*
    val BIOS_MEM = Module(new BIOS())
  */
  // This is for instruction memory (pc)
  /*
    val BIOS_MEM = Module(new BIOS())
    BIOS_MEM.io.enA := 1.U
    BIOS_MEM.io.addrA := bios_addra
    BIOS_MEM.io.dataOutA := bios_douta
  */
  // This is for data memory (alu)
  /*
    BIOS_MEM.io.enB := 1.U
    BIOS_MEM.io.addrB := bios_addrb
    BIOS_MEM.io.dataOutB := bios_doutb
  */

  // Data Memory
  // Synchronous read: read takes one cycle
  // Synchronous write: write takes one cycle
  // Write-byte-enable: select which of the four bytes to write
  /*
    val dmem_addr = Wire(UInt(14.W))
    val dmem_din, dmem_dout = Wire(UInt(32.W))
    val dmem_we = Wire(UInt(4.W))
  */
  // Instanitiate DATA MEMORY
  /*
    val DMEM = Module(new DMEM())
    DMEM.io.enable := 1.U
    DMEM.io.we := dmem_we
    DMEM.io.addr := dmem_addr
    DMEM.io.dataIn := dmem_din
    DMEM.io.dataOut := dmem_dout
  */


  // Instruction Memory
  // Synchronous read: read takes one cycle
  // Synchronous write: write takes one cycle
  // Write-byte-enable: select which of the four bytes to write
  /*
    val imem_dina, imem_doutb = Wire(UInt(32.W))
    val imem_addra, imem_addr = Wire(UInt(14.W))
    val imem_we = Wire(UInt(4.W))
    val imem_ena = Wire(UInt(1.W))
  */
  // Instanitiate INSTRUCTION MEMORY
  /*
    val IMEM = Module(new IMEM())
    IMEM.io.enable := 1.U
  */
  // data memory
  /*
    IMEM.io.we := imem_we
    IMEM.io.addrA := imem_addra
    IMEM.io.dataInA := imem_dina
  */
  // instruction memory
  /*
    IMEM.io.addrB := imem_addra
    IMEM.io.dataOutB := imem_doutb
  */