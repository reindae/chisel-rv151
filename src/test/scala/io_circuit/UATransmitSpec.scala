// See README.md for license details.

package uart_transmitter

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class UATransmitSpec extends AnyFreeSpec with ChiselScalatestTester {

  "uart transmitter should send a byte" in {
    test(new uart_transmitter(CLOCK_FREQ = 125000000, BAUD_RATE = 112500)) { dut =>
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
      dut.io.serial_out.expect(0.U)
      dut.clock.step(1)

      // check if transmitter is ready to transmit
      dut.io.ready.expect(false.B)
      dut.io.serial_out.expect(1.U)
      dut.clock.step(1)

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


    }
  }
}