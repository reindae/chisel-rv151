// See README.md for license details.

package mux_rs2

import chisel3._
import chisel3.util._

/**
 * A mux to choose between alu_out or rs2; forwarding logic case for RISC-V Core
 */

class mux_rs2 extends Module {
  val io = IO(new Bundle {
    val rs2 = Input(UInt(32.W))
    val alu_out = Input(UInt(32.W))
    val sel = Input(Bool())
    val rs2_out = Output(UInt(32.W))
  })
  
  io.rs2_out := Mux(io.sel, io.alu_out, io.rs2)

}