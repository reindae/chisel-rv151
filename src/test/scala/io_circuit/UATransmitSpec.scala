// See README.md for license details.

package uart_transmitter

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class UATransmitSpec extends AnyFreeSpec with ChiselScalatestTester {

  "uart transmitter should send a byte" in {
    val CLOCK_FREQ = 1000 //125000000
    val BAUD_RATE = 100 //112500
    val cyclesPerSymbol = (CLOCK_FREQ / BAUD_RATE).toInt
    test(new uart_transmitter(CLOCK_FREQ, BAUD_RATE)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // reset
      dut.io.reset.poke(true.B)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.io.reset.poke(false.B)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)

      // check if transmitter is ready to receive data
      dut.io.ready.expect(true.B)

      // transfer data into transmitter
      dut.io.valid.poke(true.B)
      dut.io.data_in.poke(0xab.U)
      dut.clock.step(1)
      dut.io.ready.expect(false.B)

      // transmitter should begin sending the start bit
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }

      // transmitter should send each data bit
      for (bitIndex <- 0 until 8) {
        for (i <- 0 until cyclesPerSymbol) {
          dut.io.serial_out.expect((0xab >> bitIndex) & 0x1)
          dut.clock.step(1)
        }
      }

      // transmitter should begin sending the stop bit
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }

      // check if transmitter is ready to transmit
      dut.io.serial_out.expect(1.U)
      dut.io.ready.expect(true.B)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.io.ready.expect(true.B)

      /*
      // start to transmit and check every bit
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      */
    }
  }
}
