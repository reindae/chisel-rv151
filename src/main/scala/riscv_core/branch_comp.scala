// See README.md for license details.

package branch_comp

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * branch comparator for RISC-V Core
 */

class branch_comp extends Module {
  val io = IO(new Bundle {
    val rs1 = Input(UInt(32.W))
    val rs2 = Input(UInt(32.W))
    val BrUn = Input(Bool())
    val BrEq = Output(Bool())
    val BrLt = Output(Bool())
  })
  
  when (io.BrUn === true.B) {
    io.BrLt := io.rs1 < io.rs2
    io.BrEq := io.rs1 === io.rs2
  }
  .otherwise {
    io.BrLt := io.rs1.asSInt < io.rs2.asSInt
    io.BrEq := io.rs1.asSInt === io.rs2.asSInt   
  }

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new branch_comp)
}
