circuit mux_dmem_data :
  module mux_dmem_data :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip rs2 : UInt<32>, flip alu_out : UInt<32>, flip mem_out : UInt<32>, flip sel : UInt<2>, dmem_data : UInt<32>}

    node _io_dmem_data_T = eq(UInt<1>("h0"), io.sel) @[Mux.scala 81:61]
    node _io_dmem_data_T_1 = mux(_io_dmem_data_T, io.alu_out, io.alu_out) @[Mux.scala 81:58]
    node _io_dmem_data_T_2 = eq(UInt<1>("h1"), io.sel) @[Mux.scala 81:61]
    node _io_dmem_data_T_3 = mux(_io_dmem_data_T_2, io.rs2, _io_dmem_data_T_1) @[Mux.scala 81:58]
    node _io_dmem_data_T_4 = eq(UInt<2>("h2"), io.sel) @[Mux.scala 81:61]
    node _io_dmem_data_T_5 = mux(_io_dmem_data_T_4, io.mem_out, _io_dmem_data_T_3) @[Mux.scala 81:58]
    io.dmem_data <= _io_dmem_data_T_5 @[mux_dmem_data.scala 22:16]

