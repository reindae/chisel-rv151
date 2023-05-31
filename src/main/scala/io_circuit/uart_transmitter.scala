// See README.md for license details.

package uart_transmitter

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A uart transmitter circuit (a fifo_rx) that receives a 8-bit word from a producer block on FPGA via the ready-valid interface.
 * Once we have a 8-bit word that we want to send (i.e., once valid is high, and the transmitter is ready),
 * transmitting it involves shifting each bit of the data[7:0] bus, plus the start and stop bits, out of a shift register on to the serial line.
 * The transmitted output attains the same bit for cyclesPerSymbol cycles.
 * @param CLOCK_FREQ -> clock frequency
 * @param BAUD_RATE -> baud rate, represents the number of bits per second that can be received
 * SYMBOL_EDGE_TIME: width of a bit in cycles of the system clock -> CLOCK_FREQ / BAUD_RATE
 */

class uart_transmitter(val CLOCK_FREQ: Int = 125000000, val BAUD_RATE: Int = 115200) extends Module {
  val io = IO(new Bundle {
    val reset = Input(Bool())
    val data_in = Input(UInt(8.W))
    val valid = Input(Bool())
    val ready = Output(Bool())
    val serial_out = Output(Bool())
  })

  // Internal signals
  val cyclesPerSymbol = (CLOCK_FREQ.toDouble / BAUD_RATE.toDouble).toInt
  val symbol_edge = Wire(Bool())
  val start = Wire(Bool())
  val tx_running = Wire(Bool())

  // Counters & Shift register
  val tx_shift = RegInit(0.U(10.W))
  val bit_cnt = Counter(10)
  val clkcyc_cnt = Counter(cyclesPerSymbol.toInt)


  //--|Signal Assignments|------------------------------------------------------
  // symbol_edge -> when cycle counter hits the number of cycles of one symbol (bit) transmission
  symbol_edge := clkcyc_cnt.value === (cyclesPerSymbol.U - 1.U)

  // start -> when both valid signal from rx and ready signal agrees to transmit a 8-bit character
  start := io.valid && io.ready

  // tx_running -> bit counter counting indicates the transmitter is running (sending bits)
  tx_running := bit_cnt.value =/= 0.U

  // io.ready -> When not sending bits, transmitter is ready to receive data
  io.ready := !tx_running

  // io.serial_out -> The output signal has to be 1. the first bit of shift register; 2. within the entire cycle of symbol
  io.serial_out := tx_shift(0)


  //--|Counters|----------------------------------------------------------------
  // clkcyc -> counts clock cycles until a single symbol is done
  when (start || io.reset || symbol_edge) {
    clkcyc_cnt.reset()
  }
  .otherwise {
    clkcyc_cnt.inc()
  }

  // bit -> counts down 1 for every character whenever cycles of cycles-per-symbol are counted (1 bit is sent)
  when (start) {
    bit_cnt.value := 10.U
  }
  .elsewhen (symbol_edge && tx_running) {
    bit_cnt.value := bit_cnt.value - 1.U
  }


  //--|Shift Register|----------------------------------------------------------
  when (io.reset) {
    tx_shift := "b1111111111".U
  }
  when (start) {
    tx_shift := Cat(1.U, io.data_in, 0.U)
  }
  .elsewhen (symbol_edge && tx_running) {
    tx_shift := Cat(1.U, tx_shift(9, 1))
  }

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new uart_transmitter)
}