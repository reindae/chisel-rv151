// See README.md for license details.

package uart_receiver

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class UAReceiverSpec extends AnyFreeSpec with ChiselScalatestTester {

  "uart receiver should receive a byte" in {
    val CLOCK_FREQ = 1000 //125000000
    val BAUD_RATE = 100 //112500
    val cyclesPerSymbol = (CLOCK_FREQ / BAUD_RATE).toInt
    test(new uart_receiver(CLOCK_FREQ, BAUD_RATE)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // reset
      dut.io.reset.poke(true.B)
      dut.clock.step(1)
      dut.io.reset.poke(false.B)
      dut.clock.step(1)

      // check if receiver is ready to receive data over serial line
      dut.io.data_out.expect(0xff.U)
      dut.io.valid.expect(false.B)

      // receiver should begin receive the start bit
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(0.U)
        dut.clock.step(1)
      }

      // receiver should receive each data bit
      // character 'B' (1011)
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(1.U)
        dut.clock.step(1)
      }

      // character 'A' (1010)
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(1.U)
        dut.clock.step(1)
      }

      // receiver should begin receive the stop bit
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_in.poke(1.U)
        dut.clock.step(1)
      }

      // check if receiver contains valid and correct data
      dut.io.data_out.expect(0xab.U)
      dut.io.valid.expect(true.B)

      // reset & check status
      dut.io.reset.poke(true.B)
      dut.clock.step(1)
      dut.io.reset.poke(false.B)
      dut.clock.step(1)
      dut.io.data_out.expect(0xff.U)
      dut.io.valid.expect(false.B) 

    }
  }
}
