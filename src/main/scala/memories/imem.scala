// See README.md for license details.

package imem

import chisel3._
import chisel3.util._


/**
 * IMEM for RISC-V Core
 */

class IMEM extends Module {
  val width: Int = 32
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val write = Input(Bool())
    val wea = Input(UInt(4.W))
    val addrA = Input(UInt(14.W))
    val addrB = Input(UInt(14.W))
    val dataInA = Input(UInt(width.W))
    val dataOutB = Output(UInt(width.W))
  })

  val mem = SyncReadMem(16384, UInt(width.W))
  // Create one write port (addrA) and one read port (addrB)
  // mem.write(io.addrA, io.dataInA)
  io.dataOutB := mem.read(io.addrB, io.enable)

  when (io.enable) {
    for (i <- 0 until 4) {
      when (io.wea(i)) {
        mem.write(io.addrA((i * 8.U + 8.U - 1.U), (i * 8.U)), io.dataInA((i * 8.U + 8.U - 1.U), (i * 8.U)))
      }
    }
  }

}