circuit mux_alu_b :
  module mux_alu_b :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip rs2 : UInt<32>, flip imm : UInt<32>, flip alu_out : UInt<32>, flip mem_out : UInt<32>, flip b_sel : UInt<2>, alu_b : UInt<32>}

    node _io_alu_b_T = eq(UInt<1>("h1"), io.b_sel) @[Mux.scala 81:61]
    node _io_alu_b_T_1 = mux(_io_alu_b_T, io.imm, io.rs2) @[Mux.scala 81:58]
    node _io_alu_b_T_2 = eq(UInt<2>("h2"), io.b_sel) @[Mux.scala 81:61]
    node _io_alu_b_T_3 = mux(_io_alu_b_T_2, io.alu_out, _io_alu_b_T_1) @[Mux.scala 81:58]
    node _io_alu_b_T_4 = eq(UInt<2>("h3"), io.b_sel) @[Mux.scala 81:61]
    node _io_alu_b_T_5 = mux(_io_alu_b_T_4, io.mem_out, _io_alu_b_T_3) @[Mux.scala 81:58]
    io.alu_b <= _io_alu_b_T_5 @[mux_alu_b.scala 23:12]

