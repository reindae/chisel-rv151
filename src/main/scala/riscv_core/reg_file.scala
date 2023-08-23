// See README.md for license details.

package reg_file

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * register file for RISC-V Core
 */

class reg_file extends Module {
  val io = IO(new Bundle {
    val we = Input(Bool())
    val rs1 = Input(UInt(5.W))
    val rs2 = Input(UInt(5.W))
    val wa = Input(UInt(5.W))
    val wd = Input(UInt(32.W))
    val rd1 = Output(UInt(32.W))
    val rd2 = Output(UInt(32.W))
  })

  val DEPTH = 32.U
  val mem = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  // synchronous-write port
  when (io.we) {
    mem(io.wa) := io.wd
  }

  // asynchronous-read ports.
  io.rd1 := mem(io.rs1)
  io.rd2 := mem(io.rs2)

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new reg_file)
}
