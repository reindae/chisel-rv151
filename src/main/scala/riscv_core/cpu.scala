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
import chisel3._
import chisel3.util._


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

  


}
