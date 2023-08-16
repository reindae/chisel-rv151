// See README.md for license details.

package bios_mem

import chisel3._
import chisel3.util._


/**
 * bios memory for RISC-V Core
 */

class BIOS extends Module {
  val io = IO(new Bundle {
    val enA = Input(Bool())
    val enB = Input(Bool())
    val write = Input(Bool())
    val addrA = Input(UInt(12.W))
    val addrB = Input(UInt(12.W))
    val dataOutA = Input(UInt(32.W))
    val dataOutB = Output(UInt(32.W))
  })

  val mem = SyncReadMem(4096, UInt(32.W))
  // Create two read port (addrA and addrB)

  io.dataOutA := mem.read(io.addrA, io.enA)
  io.dataOutB := mem.read(io.addrB, io.enB)

  // setup simulation

}