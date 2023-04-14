// See README.md for license details.

package data_out

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * data_out logic for RISC-V Core
 */

class data_out extends Module {
  val io = IO(new Bundle {
    val rst = Input(Bool())
    val sign_ext = Input(Bool())
    val dmem_dout = Input(UInt(32.W))
    val bios_doutb = Input(UInt(32.W))
    val rx_fifo_empty = Input(Bool())
    val tx_fifo_full = Input(Bool())
    val rx_fifo_out = Input(UInt(8.W))
    val cycle_p = Input(UInt(32.W))
    val inst_p = Input(UInt(32.W))
    val corr_B_p = Input(UInt(32.W))
    val total_B_p = Input(UInt(32.W))
    val mem_out = Input(UInt(4.W))
    val prev_data_addr = Input(UInt(32.W))
	  val uart_rx_data_out_valid = Input(Bool())
	  val uart_tx_data_in_ready = Input(Bool())
    val data_out = Output(UInt(32.W))
  })

  val data = Wire(UInt(32.W))

  when (io.rst) {
    data := 0.U
  }
  .otherwise {
    data := MuxLookup(io.prev_data_addr(31,28), 0.U, Array(
      "b0001".U(4.W) -> io.dmem_dout,
      "b0011".U(4.W) -> io.dmem_dout,
      "b0100".U(4.W) -> io.bios_doutb,
      "b1000".U(4.W) -> MuxLookup(io.prev_data_addr, 0.U, Array(
        "x80000000".U -> Cat(0.U(30.W), !io.rx_fifo_empty, io.uart_tx_data_in_ready),
        "x80000004".U -> Cat(0.U(24.W), io.rx_fifo_out),
        "x80000010".U -> io.cycle_p,
        "x80000014".U -> io.inst_p,
        "x8000001c".U -> io.total_B_p,
        "x80000020".U -> io.corr_B_p
      ))
    ))
  }

  // To handle sign extension.
  when (io.rst) {
    io.data_out := 0.U
  }
  .otherwise {
    io.data_out := MuxLookup(io.mem_out, 0.U, Array(
      "b0001".U(4.W) -> Cat(Fill(24, Mux(io.sign_ext, data(7), 0.U)), data(7,0)),
      "b0010".U(4.W) -> Cat(Fill(24, Mux(io.sign_ext, data(15), 0.U)), data(15,8)),
      "b0100".U(4.W) -> Cat(Fill(24, Mux(io.sign_ext, data(23), 0.U)), data(23,16)),
      "b1000".U(4.W) -> Cat(Fill(24, Mux(io.sign_ext, data(31), 0.U)), data(31,24)),
      "b0011".U(4.W) -> Cat(Fill(16, Mux(io.sign_ext, data(15), 0.U)), data(15,0)),
      "b1100".U(4.W) -> Cat(Fill(16, Mux(io.sign_ext, data(31), 0.U)), data(31,16)),
      "b1111".U(4.W) -> data
    ))
  }

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new data_out)
}
