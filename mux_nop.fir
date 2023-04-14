circuit mux_nop :
  module mux_nop :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip pc_4 : UInt<32>, flip alu_out : UInt<32>, flip pc_sel : UInt<1>, inst : UInt<32>}

    node _io_inst_T = mux(io.pc_sel, UInt<5>("h13"), io.pc_4) @[mux_nop.scala 21:17]
    io.inst <= _io_inst_T @[mux_nop.scala 21:11]

