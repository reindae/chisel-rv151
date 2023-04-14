// See README.md for license details.

package mux_csr

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose between 27'b0 + rs1 or rd1; CSR inst for RISC-V Core
 */

class mux_csr extends Module {
  val io = IO(new Bundle {
    val rs1 = Input(UInt(5.W))
    val rd1 = Input(UInt(32.W))
    val csr_sel = Input(Bool())
    val csr_din = Output(UInt(32.W))
  })
  
  io.csr_din := Mux(io.csr_sel, Cat(0.U(27.W), io.rs1), io.rd1)

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_csr)
}