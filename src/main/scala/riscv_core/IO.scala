// See README.md for license details.

package IO

import opcode._
import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * IO logic for RISC-V Core
 */

class IO extends Module {
  val io = IO(new Bundle {
    val rst = Input(Bool())
    val data_addr = Input(UInt(32.W))
    val s3_inst = Input(UInt(32.W))
    val cycle_p = Output(UInt(32.W))
    val inst_p = Output(UInt(32.W))
    val corr_B_p = Output(UInt(32.W))
    val total_B_p = Output(UInt(32.W))
  })

  val prev_inst = Reg(UInt(32.W))
  val cycle_p = Reg(UInt(32.W))
  val inst_p = Reg(UInt(32.W))
  val corr_B_p = Reg(UInt(32.W))
  val total_B_p = Reg(UInt(32.W))

  io.cycle_p := cycle_p
  io.inst_p := inst_p
  io.corr_B_p := corr_B_p
  io.total_B_p := total_B_p

  when (io.rst || (io.data_addr === "x80000018".U)) {
    cycle_p := 0.U
    inst_p := 0.U
    corr_B_p := 0.U
    total_B_p := 0.U
    prev_inst := 0x13.U
  }
  .otherwise {
    cycle_p := cycle_p + 1.U
    when (io.s3_inst =/= 0x13.U && io.s3_inst =/= prev_inst) {
      prev_inst := io.s3_inst
      inst_p := inst_p + 1.U
    }
    when (io.s3_inst(6,0) === opcode.OPC_BRANCH) {
      total_B_p := total_B_p + 1.U
    }
  }

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new IO)
}
