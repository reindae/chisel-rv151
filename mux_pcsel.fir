circuit mux_pcsel :
  module mux_pcsel :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip pc_4 : UInt<32>, flip alu_out : UInt<32>, flip pc_sel : UInt<1>, pc_out : UInt<32>}

    node _io_pc_out_T = mux(io.pc_sel, io.alu_out, io.pc_4) @[mux_pcsel.scala 21:19]
    io.pc_out <= _io_pc_out_T @[mux_pcsel.scala 21:13]

