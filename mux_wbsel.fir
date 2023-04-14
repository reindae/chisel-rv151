circuit mux_wbsel :
  module mux_wbsel :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip pc : UInt<32>, flip alu_out : UInt<32>, flip data_out : UInt<32>, flip wb_sel : UInt<2>, wb_out : UInt<32>}

    node _io_wb_out_T = add(io.pc, UInt<3>("h4")) @[mux_wbsel.scala 22:44]
    node _io_wb_out_T_1 = tail(_io_wb_out_T, 1) @[mux_wbsel.scala 22:44]
    node _io_wb_out_T_2 = add(io.pc, UInt<3>("h4")) @[mux_wbsel.scala 23:19]
    node _io_wb_out_T_3 = tail(_io_wb_out_T_2, 1) @[mux_wbsel.scala 23:19]
    node _io_wb_out_T_4 = eq(UInt<1>("h0"), io.wb_sel) @[Mux.scala 81:61]
    node _io_wb_out_T_5 = mux(_io_wb_out_T_4, _io_wb_out_T_3, _io_wb_out_T_1) @[Mux.scala 81:58]
    node _io_wb_out_T_6 = eq(UInt<1>("h1"), io.wb_sel) @[Mux.scala 81:61]
    node _io_wb_out_T_7 = mux(_io_wb_out_T_6, io.alu_out, _io_wb_out_T_5) @[Mux.scala 81:58]
    node _io_wb_out_T_8 = eq(UInt<2>("h2"), io.wb_sel) @[Mux.scala 81:61]
    node _io_wb_out_T_9 = mux(_io_wb_out_T_8, io.data_out, _io_wb_out_T_7) @[Mux.scala 81:58]
    io.wb_out <= _io_wb_out_T_9 @[mux_wbsel.scala 22:13]

