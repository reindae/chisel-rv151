// See README.md for license details.

package uart

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._

class UT2R(val CLOCK_FREQ: Int = 125000000, val BAUD_RATE: Int = 115200) extends Module {
  val io = IO(new Bundle {
    val reset = Input(Bool())

    val data_in = Input(UInt(8.W))
    val data_in_valid = Input(Bool())
    val data_in_ready = Output(Bool())

    val data_out = Output(UInt(8.W))
    val data_out_valid = Output(Bool())
    val data_out_ready = Input(Bool())
  })

  val serial_rx, serial_tx = Wire(UInt(1.W))

  val offChipUA = Module(new uart(CLOCK_FREQ, BAUD_RATE))
  offChipUA.io.reset := io.reset
  offChipUA.io.data_in := io.data_in
  offChipUA.io.data_in_valid := io.data_in_valid
  io.data_in_ready := offChipUA.io.data_in_ready
  // offChipUA.io.data_out := 0x00.U                // We aren't using the receiver of the off-chip UART, only the transmitter
  // offChipUA.io.data_out_valid := false.B
  offChipUA.io.data_out_ready := false.B
  offChipUA.io.serial_in := serial_rx               // offChip might receive data from onChip since 2 UARTs can be bidirectional
  serial_tx := offChipUA.io.serial_out

  val onChipUA = Module(new uart(CLOCK_FREQ, BAUD_RATE))
  onChipUA.io.reset := io.reset
  onChipUA.io.data_in := 0x00.U                     // We aren't using the transmitter of the on-chip UART, only the receiver
  onChipUA.io.data_in_valid := 0.B
  // onChipUA.io.data_in_ready := false.B
  io.data_out := onChipUA.io.data_out
  io.data_out_valid := onChipUA.io.data_out_valid
  onChipUA.io.data_out_ready := io.data_out_ready
  onChipUA.io.serial_in := serial_tx                // Notice these lines are connected opposite to the off_chip_uart
  serial_rx := onChipUA.io.serial_out
}

class UT2RSpec extends AnyFreeSpec with ChiselScalatestTester {

  "U_T should send data to U_R properly" in {
    val CLOCK_FREQ = 1000
    val BAUD_RATE = 100
    val cyclesPerSymbol = (CLOCK_FREQ / BAUD_RATE).toInt
    test(new UT2R(CLOCK_FREQ, BAUD_RATE)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // reset
      dut.io.reset.poke(true.B)
      dut.clock.step(1)
      dut.io.reset.poke(false.B)
      dut.clock.step(1)

      // check if offChip is ready to get data for transmission
      dut.io.data_in_ready.expect(true.B)

      // check if onChip is ready to receive data from offChip over serial line
      dut.io.data_out.expect(0xff.U)
      dut.io.data_in_valid.expect(false.B)

      // inject initial data (0xab) to offChip, and check output status is going to send and not receiving anymore data
      dut.io.data_in_valid.poke(true.B)
      dut.io.data_in.poke(0xab.U)
      dut.clock.step(1)
      dut.io.data_in_valid.poke(false.B)
      dut.io.data_in_ready.expect(false.B)

      // offChip should begin sending the start bit
      for (i <- 0 until cyclesPerSymbol) {
        // dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }

      // offChip should send each data bit,
      // onChip should receive each data bit concurrently
      // character 'B' (1011)
      for (bitWidth <- 0 until 4) {
        for (i <- 0 until cyclesPerSymbol) {
          dut.clock.step(1)
        }
      }

      // character 'A' (1010)
      for (bitWidth <- 0 until 4) {
        for (i <- 0 until cyclesPerSymbol) {
          dut.clock.step(1)
        }
      }

      // offChip should send the stop bit
      for (i <- 0 until cyclesPerSymbol) {
        // dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }

      // check if onChip contains valid and correct data
      dut.io.data_out.expect(0xab.U)
      dut.io.data_out_valid.expect(true.B)

      // wait to see if onChip still holds the data
      dut.clock.step(10)
      dut.io.data_out.expect(0xab.U)

      // check if transmitter is ready to get data for transmission
      // dut.io.serial_out.expect(1.U)
      dut.io.data_in_ready.expect(true.B)
      dut.clock.step(1)
      // dut.io.serial_out.expect(1.U)
      dut.io.data_in_ready.expect(true.B)


    }
  }
}

