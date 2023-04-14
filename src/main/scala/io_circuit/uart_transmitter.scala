// See README.md for license details.

package uart_transmitter

import chisel3._
import chisel3.util._

/**
 * A uart transmitter circuit (a fifo_rx) that receives a 8-bit word from a producer block on FPGA via the ready-valid interface.
 * Once we have a 8-bit word that we want to send (i.e., once valid is high, and the transmitter is ready),
 * transmitting it involves shifting each bit of the data[7:0] bus, plus the start and stop bits, out of a shift register on to the serial line.
 * @param CLOCK_FREQ -> clock frequency
 * @param BAUD_RATE -> baud rate, represents the number of bits per second that can be received
 * SYMBOL_EDGE_TIME: width of a bit in cycles of the system clock -> CLOCK_FREQ / BAUD_RATE
 */

class uart_transmitter(val CLOCK_FREQ: Int = 125000000, val BAUD_RATE: Int = 115200) extends Module {
  val io = IO(new Bundle {

    val data_in = Input(UInt(8.W))
    val valid = Input(Bool())
    val ready = Output(Bool())
    val serial_out = Output(UInt(1.W))

  })

  val SYMBOL_EDGE_TIME = CLOCK_FREQ / BAUD_RATE
  val SAMPLE_TIME = SYMBOL_EDGE_TIME / 2

  val symbol_edge = Wire(Bool())
  val start = Wire(Bool())
  val tx_running = Wire(Bool())

  val tx_shift = RegInit(0.U(8.W))
  val bit_cnt = Counter(11)
  val clk_cnt = Counter(SYMBOL_EDGE_TIME)

  //--|Signal Assignments|------------------------------------------------------

  // Goes high at every symbol edge
  symbol_edge := clk_cnt.value === (SYMBOL_EDGE_TIME.U - 1.U)

  // Goes high halfway through each symbol
  // sample := clk_cnt.value === SAMPLE_TIME.U

  // Goes high when it is time to start transmit a new character
  start := io.valid && io.ready

  // Goes high while transmitting a character
  tx_running := bit_cnt.value =/= 0.U

  // When not transmitting, transmitter is ready to receive data
  io.ready := !tx_running


  //--|Counters|----------------------------------------------------------------

  // Counts cycles until a single symbol is done
  when (start || io.reset || symbol_edge) {
    clk_cnt.reset()
  }
  .otherwise {
    clk_cnt.inc()
  }

  // Counts down from 10 bits for every character
  when (io.reset) {
    bit_cnt.reset()
  }
  .elsewhen (start) {
    bit_cnt.value := 10.U
  }
  .elsewhen (symbol_edge && tx_running) {
    bit_cnt.value := bit_cnt.value - 1.U
  }


  //--|Shift Register|----------------------------------------------------------
  when (start) {
    io.serial_out := 0.U
    tx_shift := io.data_in
  }
  .elsewhen (symbol_edge) {
    io.serial_out := tx_shift(0)
    tx_shift := Cat(1.U, tx_shift(7, 1))
  }
  .otherwise {
    io.serial_out := 1.U
  }

}

