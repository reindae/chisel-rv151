// See README.md for license details.

package uart_receiver

import chisel3._
import chisel3.util._

/**
 * A uart receiver circuit that functions as a shift register that shifts bits in from the serial line.
 * It sends the received 8-bit word to a consumer block on the FPGA via a ready-valid interface. (act as a source)
 * Once we have received a full UART packet over the serial port, the valid signal should go high until the ready signal goes high,
 * after which the valid signal will be driven low until we receive another UART packet.
 * @param CLOCK_FREQ -> clock frequency
 * @param BAUD_RATE -> baud rate, represents the number of bits per second that can be received
 * SYMBOL_EDGE_TIME: width of a bit in cycles of the system clock -> CLOCK_FREQ / BAUD_RATE
 */

class uart_receiver(val CLOCK_FREQ: Int = 125000000, val BAUD_RATE: Int = 115200) extends Module {
  val io = IO(new Bundle {

    val reset = Input(Bool())
    val serial_in = Input(UInt(1.W))
    val ready = Input(Bool())
    val valid = Output(Bool())
    val data_out = Output(UInt(8.W))

  })

  val SYMBOL_EDGE_TIME = CLOCK_FREQ / BAUD_RATE
  val SAMPLE_TIME = SYMBOL_EDGE_TIME / 2

  val symbol_edge = Wire(Bool())
  val sample = Wire(Bool())
  val start = Wire(Bool())
  val rx_running = Wire(Bool())

  val rx_shift = RegInit(0.U(10.W))
  val bit_cnt = Counter(11)
  val clk_cnt = Counter(SYMBOL_EDGE_TIME)
  val has_byte = RegInit(0.U)

  //--|Signal Assignments|------------------------------------------------------

  // Goes high at every symbol edge
  symbol_edge := clk_cnt.value === (SYMBOL_EDGE_TIME - 1).U

  // Goes high halfway through each symbol
  sample := clk_cnt.value === SAMPLE_TIME.U

  // Goes high when it is time to start receiving a new character
  start := (io.serial_in =/= 0.U) && !rx_running

  // Goes high while we are receiving a character
  rx_running := bit_cnt.value =/= 0.U

  // Outputs
  io.data_out := rx_shift(8, 1)
  io.valid := (has_byte === 1.U) && !rx_running


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
  .elsewhen (symbol_edge && rx_running) {
    bit_cnt.value := bit_cnt.value - 1.U
  }


  //--|Shift Register|----------------------------------------------------------
  when (sample && rx_running) {
    rx_shift := Cat(io.serial_in, rx_shift(9, 1))
  }


  //--|Extra State For Ready/Valid|---------------------------------------------
  // This block and the has_byte signal aren't needed in the uart_transmitter
  when (io.reset) {
    has_byte := 0.U
  }
  .elsewhen (bit_cnt.value === 1.U && symbol_edge) {
    has_byte := 1.U
  }
  .elsewhen (io.ready) {
    has_byte := 0.U
  }

}
