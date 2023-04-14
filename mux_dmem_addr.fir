circuit mux_dmem_addr :
  module mux_dmem_addr :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip alu_out : UInt<32>, flip mem_out : UInt<32>, flip sel : UInt<1>, dmem_addr : UInt<32>}

    node _io_dmem_addr_T = mux(io.sel, io.mem_out, io.alu_out) @[mux_dmem_addr.scala 21:22]
    io.dmem_addr <= _io_dmem_addr_T @[mux_dmem_addr.scala 21:16]

