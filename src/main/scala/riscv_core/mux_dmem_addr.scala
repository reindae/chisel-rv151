// See README.md for license details.

package mux_dmem_addr

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose between alu result or loaded data from mem for RISC-V Core
 */

class mux_dmem_addr extends Module {
  val io = IO(new Bundle {
    val alu_out = Input(UInt(32.W))
    val mem_out = Input(UInt(32.W))
    val sel = Input(Bool())
    val dmem_addr = Output(UInt(32.W))
  })

  io.dmem_addr := Mux(io.sel, io.mem_out, io.alu_out)

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_dmem_addr)
}