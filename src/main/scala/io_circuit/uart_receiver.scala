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

  // Internal signals
  val cyclesPerSymbol = (CLOCK_FREQ.toDouble / BAUD_RATE.toDouble).toInt
  val sampleTime = cyclesPerSymbol / 2
  val sample = Wire(Bool())
  val symbol_edge = Wire(Bool())
  val start = Wire(Bool())
  val rx_running = Wire(Bool())
  val has_byte = RegInit(0.U)

  // Counters & Shift register
  val rx_shift = RegInit(0.U(10.W))
  val bit_cnt = Counter(10)
  val clkcyc_cnt = Counter(cyclesPerSymbol.toInt)


  //--|Signal Assignments|------------------------------------------------------
  // symbol_edge -> when cycle counter hits the number of cycles of one symbol (bit) transmission
  symbol_edge := clkcyc_cnt.value === (cyclesPerSymbol.U - 1.U)

  // sample -> when cycle counter counts halfway through each symbol, sample the current bit
  sample := clkcyc_cnt.value === sampleTime.U

  // start -> when both serial_in and rx_running signal becomes zero (!idle && !receiving), they agree to receiver a 8-bit character
  start := !io.serial_in && !rx_running

  // rx_running -> bit counter counting indicates the receiver is running (receving bits)
  rx_running := bit_cnt.value =/= 0.U

  // io.valid -> the available data are valid iff complete byte is receivced and rx not running anymore
  io.valid := (has_byte === 1.U) && !rx_running

  // io.data_out -> the output data toward transmitter consists of [8:1] of the shift register in rx (0 & 9 are the starting & stopping bit of rx)
  io.data_out := rx_shift(8, 1)


  //--|Counters|----------------------------------------------------------------
  // clkcyc -> counts clock cycles until a single symbol is done
  when (start || io.reset || symbol_edge) {
    clkcyc_cnt.reset()
  }
  .otherwise {
    clkcyc_cnt.inc()
  }

  // bit -> counts down 1 for every character whenever cycles of cycles-per-symbol are counted (1 bit is received)
  when (start) {
    bit_cnt.value := 10.U
  }
  .elsewhen (symbol_edge && rx_running) {
    bit_cnt.value := bit_cnt.value - 1.U
  }


  //--|Shift Register|----------------------------------------------------------
  when (io.reset) {
    rx_shift := "b1111111111".U
  }
  .elsewhen (sample && rx_running) {
    rx_shift := Cat(io.serial_in, rx_shift(9, 1))
  }



  //--|Extra State For Ready/Valid|---------------------------------------------
  // This block and the has_byte signal aren't needed in the uart_transmitter
  when (io.reset || io.ready) {
    has_byte := 0.U
  }
  .elsewhen (bit_cnt.value === 1.U && symbol_edge) {
    has_byte := 1.U
  }

}
