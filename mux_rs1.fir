circuit mux_rs1 :
  module mux_rs1 :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip rs1 : UInt<32>, flip alu_out : UInt<32>, flip sel : UInt<1>, rs1_out : UInt<32>}

    node _io_rs1_out_T = mux(io.sel, io.alu_out, io.rs1) @[mux_rs1.scala 21:20]
    io.rs1_out <= _io_rs1_out_T @[mux_rs1.scala 21:14]

