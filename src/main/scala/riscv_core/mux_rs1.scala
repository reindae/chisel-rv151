// See README.md for license details.

package mux_rs1

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose between alu_out or rs1; forwarding logic case for RISC-V Core
 */

class mux_rs1 extends Module {
  val io = IO(new Bundle {
    val rs1 = Input(UInt(32.W))
    val alu_out = Input(UInt(32.W))
    val sel = Input(Bool())
    val rs1_out = Output(UInt(32.W))
  })
  
  io.rs1_out := Mux(io.sel, io.alu_out, io.rs1)

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_rs1)
}