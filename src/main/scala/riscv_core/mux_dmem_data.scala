// See README.md for license details.

package mux_dmem_data

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose among alu result, loaded data from mem, or rs2; forwarding purpose (hazard) for RISC-V Core
 */

class mux_dmem_data extends Module {
  val io = IO(new Bundle {
    val rs2 = Input(UInt(32.W))
    val alu_out = Input(UInt(32.W))
    val mem_out = Input(UInt(32.W))
    val sel = Input(UInt(2.W))
    val dmem_data = Output(UInt(32.W))
  })

  io.dmem_data := MuxLookup(io.sel, io.alu_out, Array(
    0.U -> io.alu_out,
    1.U -> io.rs2,
    2.U -> io.mem_out
  ))

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_dmem_data)
}