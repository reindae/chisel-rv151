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

      // transfer data into transmitter, and check output status is going to send and not receiving anymore data
      dut.io.valid.poke(true.B)
      dut.io.data_in.poke(0xab.U)
      dut.clock.step(1)
      dut.io.valid.poke(false.B)
      dut.io.ready.expect(false.B)

      // transmitter should begin sending the start bit
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }

      // transmitter should send each data bit
      // character 'B' (1011)
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }

      // character 'A' (1010)
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }

      // The computation does not work inside test case
      // for (bitIndex <- 0 until 8) {
      //   for (i <- 0 until cyclesPerSymbol) {
      //     dut.io.serial_out.expect((0xab.U >> bitIndex) & 0x1.U)
      //     dut.clock.step(1)
      //   }
      // }

      // transmitter should begin sending the stop bit
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }


      // check if transmitter is ready to receive data
      dut.io.serial_out.expect(1.U)
      dut.io.ready.expect(true.B)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.io.ready.expect(true.B)


      // send another byte (0xcd)
      dut.io.valid.poke(true.B)
      dut.io.data_in.poke(0xcd.U)
      dut.clock.step(1)
      dut.io.valid.poke(false.B)
      dut.io.ready.expect(false.B)

      // transmitter should begin sending the start bit
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }

      // transmitter should send each data bit
      // character 'D' (1101)
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }

      // character 'C' (1100)
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(0.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }

      // transmitter should begin sending the stop bit
      for (i <- 0 until cyclesPerSymbol) {
        dut.io.serial_out.expect(1.U)
        dut.clock.step(1)
      }


      // check if transmitter is ready to receive data
      dut.io.serial_out.expect(1.U)
      dut.io.ready.expect(true.B)
      dut.clock.step(1)
      dut.io.serial_out.expect(1.U)
      dut.io.ready.expect(true.B)
    }
  }
}
