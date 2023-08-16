// See README.md for license details.

package dmem

import chisel3._
import chisel3.util._


/**
 * DMEM for RISC-V Core
 */

class DMEM extends Module {
  val width: Int = 32
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val write = Input(Bool())
    val we = Input(UInt(4.W))
    val addr = Input(UInt(14.W))
    val dataIn = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
  })

  val mem = SyncReadMem(16384, UInt(width.W))
  // Create one write port (addr) and one read port (addr)

  when (io.enable) {
    for (i <- 0 until 4) {
      when (io.we(i)) {
        mem.write(io.addr((i * 8.U + 8.U - 1.U), (i * 8.U)), io.dataIn((i * 8.U + 8.U - 1.U), (i * 8.U)))
      }
    }
  }
  
  io.dataOut := mem.read(io.addr, io.enable)

}