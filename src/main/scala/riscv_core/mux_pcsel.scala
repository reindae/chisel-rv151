// See README.md for license details.

package mux_pcsel

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose between pc+4 or alu result; b-type or jump instrction cases for RISC-V Core
 */

class mux_pcsel extends Module {
  val io = IO(new Bundle {
    val pc_4 = Input(UInt(32.W))
    val alu_out = Input(UInt(32.W))
    val pc_sel = Input(Bool())
    val pc_out = Output(UInt(32.W))
  })

  io.pc_out := Mux(io.pc_sel, io.alu_out, io.pc_4)

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_pcsel)
}