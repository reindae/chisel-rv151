circuit mux_alu_a :
  module mux_alu_a :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip rs1 : UInt<32>, flip pc : UInt<32>, flip alu_out : UInt<32>, flip mem_out : UInt<32>, flip a_sel : UInt<2>, alu_a : UInt<32>}

    node _io_alu_a_T = eq(UInt<1>("h1"), io.a_sel) @[Mux.scala 81:61]
    node _io_alu_a_T_1 = mux(_io_alu_a_T, io.pc, io.rs1) @[Mux.scala 81:58]
    node _io_alu_a_T_2 = eq(UInt<2>("h2"), io.a_sel) @[Mux.scala 81:61]
    node _io_alu_a_T_3 = mux(_io_alu_a_T_2, io.alu_out, _io_alu_a_T_1) @[Mux.scala 81:58]
    node _io_alu_a_T_4 = eq(UInt<2>("h3"), io.a_sel) @[Mux.scala 81:61]
    node _io_alu_a_T_5 = mux(_io_alu_a_T_4, io.mem_out, _io_alu_a_T_3) @[Mux.scala 81:58]
    io.alu_a <= _io_alu_a_T_5 @[mux_wbsel copy.scala 23:12]

