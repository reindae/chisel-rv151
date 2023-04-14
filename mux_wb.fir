circuit mux_wb :
  module mux_wb :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip pc : UInt<32>, flip alu_out : UInt<32>, flip data_out : UInt<32>, flip wb_sel : UInt<2>, wb : UInt<32>}

    node _io_wb_T = add(io.pc, UInt<3>("h4")) @[mux_nop.scala 23:19]
    node _io_wb_T_1 = tail(_io_wb_T, 1) @[mux_nop.scala 23:19]
    node _io_wb_T_2 = eq(UInt<1>("h0"), io.wb_sel) @[Mux.scala 81:61]
    node _io_wb_T_3 = mux(_io_wb_T_2, _io_wb_T_1, UInt<1>("h0")) @[Mux.scala 81:58]
    node _io_wb_T_4 = eq(UInt<1>("h1"), io.wb_sel) @[Mux.scala 81:61]
    node _io_wb_T_5 = mux(_io_wb_T_4, io.alu_out, _io_wb_T_3) @[Mux.scala 81:58]
    node _io_wb_T_6 = eq(UInt<2>("h2"), io.wb_sel) @[Mux.scala 81:61]
    node _io_wb_T_7 = mux(_io_wb_T_6, io.data_out, _io_wb_T_5) @[Mux.scala 81:58]
    io.wb <= _io_wb_T_7 @[mux_nop.scala 22:9]

