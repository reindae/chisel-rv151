// See README.md for license details.

package mux_wbsel

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose among alu result, loaded data from mem, or pc + 4; write back to RegFile for RISC-V Core
 */

class mux_wbsel extends Module {
  val io = IO(new Bundle {
    val pc = Input(UInt(32.W))
    val alu_out = Input(UInt(32.W))
    val data_out = Input(UInt(32.W))
    val wb_sel = Input(UInt(2.W))
    val wb_out = Output(UInt(32.W))
  })

  io.wb_out := MuxLookup(io.wb_sel, (io.pc + 4.U), Array(
    0.U -> (io.pc + 4.U),
    1.U -> io.alu_out,
    2.U -> io.data_out
  ))

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_wbsel)
}