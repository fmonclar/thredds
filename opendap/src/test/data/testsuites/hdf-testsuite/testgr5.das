Attributes {
    GR_DFNT_UINT8 {
        Byte GR_ATTR_DFNT_UINT8 0, 1, 255;
    }
    GR_DFNT_INT32 {
        Int32 GR_ATTR_DFNT_INT32 0, 1, -1, 2147483647, -2147483648;
    }
    GR_DFNT_FLOAT64 {
        Float64 GR_ATTR_DFNT_FLOAT64 0, 1, -1, 1.79769e+308, 4.94066e-324, -1.79769e+308, -4.94066e-324;
    }
    GR_DFNT_CHAR8 {
    }
    GR_DFNT_UINT32 {
        UInt32 GR_ATTR_DFNT_UINT32 0, 1, 4294967295;
    }
    GR_DFNT_INT16 {
        Int32 GR_ATTR_DFNT_INT16 0, 1, -1, 32767, -32768;
    }
    GR_DFNT_UINT16 {
        UInt32 GR_ATTR_DFNT_UINT16 0, 1, 65535;
    }
    GR_DFNT_FLOAT32 {
        Float64 GR_ATTR_DFNT_FLOAT32 0, 1, -1, 3.40282e+38, 1.4013e-45, -3.40282e+38, -1.4013e-45;
    }
    GR_DFNT_UCHAR8 {
        String GR_ATTR_DFNT_UCHAR8 "\000\001\377";
    }
    GR_DFNT_INT8 {
        Int32 GR_ATTR_DFNT_INT8 0, 1, -1, 127, -128;
    }
    HDF_GLOBAL {
        Int32 GLOBAL_ATTR_DFNT_INT32 0, 1, -1, 2147483647, -2147483648;
        UInt32 GLOBAL_ATTR_DFNT_UINT32 0, 1, 4294967295;
        Int32 GLOBAL_ATTR_DFNT_INT16 0, 1, -1, 32767, -32768;
        UInt32 GLOBAL_ATTR_DFNT_UINT16 0, 1, 65535;
        Int32 GLOBAL_ATTR_DFNT_INT8 0, 1, -1, 127, -128;
        Byte GLOBAL_ATTR_DFNT_UINT8 0, 1, 255;
        Float64 GLOBAL_ATTR_DFNT_FLOAT32 0, 1, -1, 3.40282e+38, 1.4013e-45, -3.40282e+38, -1.4013e-45;
        Float64 GLOBAL_ATTR_DFNT_FLOAT64 0, 1, -1, 1.79769e+308, 4.94066e-324, -1.79769e+308, -4.94066e-324;
        Float64 GLOBAL_ATTR_DFNT_CHAR8 0, 1, -1, 1.79769e+308, 4.94066e-324, -1.79769e+308, -4.94066e-324;
        String GLOBAL_ATTR_DFNT_UCHAR8 "\000\001\377";
    }
}
