// See README.md for license details.

package fifo

import chisel3._
import chisel3.util._

/**
 * A FIFO circuit that can allow data to be stored and retrieved in the order which it was received.
 * @param WIDTH -> width of the fifo memory construct
 * @param DEPTH -> depth of the fifo memory construct
 * @param PTR_WIDTH -> width of the write and read counter
 */

class synchronizer(val WIDTH: Int, val DEPTH: Int) extends Module {
  val io = IO(new Bundle {
    /**
     * IO consists of reset, write, read signals
     * IO consists of data_in (when write) and data_out (when read)
     * The clock has been embedded into Chisel to achieve synchronization 
     */
    val rst = Input(Bool())
    val wr = Input(Bool())
    val rd = Input(Bool())
    val data_in = Input(UInt(WIDTH.W))
    val data_out = Output(UInt(WIDTH.W))
    val full = Output(Bool())
    val empty = Output(Bool())
  })
  /**
   * fifo_mem -> Total memory allocated in the parameterized FIFO, using regs of vecs
   * wr_ptr, rd_ptr -> circular buffers when performing write/read operation of FIFO,
   *                   ensures that the value wraps around to zero (overwrite/read)
   */
  val fifo_mem = RegInit(VecInit(Seq.fill(DEPTH)(0.U(WIDTH.W))))
  val wr_ptr, rd_ptr = Counter(DEPTH)
  // val rd_ptr = RegInit(0.U(log2Ceil(DEPTH).W))

  val data_out  = Reg(UInt())
  
  io.full := wr_ptr.value === (rd_ptr.value - 1.U)
  io.empty := wr_ptr.value === rd_ptr.value
  io.data_out := data_out


  // reset re-instantiate fifo buffer
  when(io.rst) {
    for (i <- 0 until DEPTH) {
      fifo_mem(i) := 0.U
    }
    wr_ptr.value := 0.U
    rd_ptr.value := 0.U
  }
  
  // write & read does not change remain memory
  .elsewhen(io.wr && io.rd && !io.empty && !io.full) {
    fifo_mem(wr_ptr.value) := io.data_in
    wr_ptr.inc()
    data_out := fifo_mem(rd_ptr.value)
    rd_ptr.inc()
  }
  
  // write only
  .elsewhen(io.wr && !io.full) {
    fifo_mem(wr_ptr.value) := io.data_in
    wr_ptr.inc()
  }
  
  // read only
 .elsewhen(io.rd && !io.empty) {
    data_out := fifo_mem(rd_ptr.value)
    rd_ptr.inc()
  }


}
