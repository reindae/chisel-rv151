circuit mux_csr :
  module mux_csr :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip rs1 : UInt<5>, flip rd1 : UInt<32>, flip csr_sel : UInt<1>, csr_din : UInt<32>}

    node _io_csr_din_T = cat(UInt<27>("h0"), io.rs1) @[Cat.scala 33:92]
    node _io_csr_din_T_1 = mux(io.csr_sel, _io_csr_din_T, io.rd1) @[mux_csr.scala 21:20]
    io.csr_din <= _io_csr_din_T_1 @[mux_csr.scala 21:14]

