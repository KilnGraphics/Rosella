package graphics.kiln.rosella.util;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;

public enum ImageFormat {
    R4G4_UNORM_PACK8(0, VK10.VK_FORMAT_R4G4_UNORM_PACK8, CompatibilityClass.BIT8, 2),
    R4G4B4A4_UNORM_PACK16(1, VK10.VK_FORMAT_R4G4B4A4_UNORM_PACK16, CompatibilityClass.BIT16, 4),
    B4G4R4A4_UNORM_PACK16(2, VK10.VK_FORMAT_B4G4R4A4_UNORM_PACK16, CompatibilityClass.BIT16, 4),
    R5G6B5_UNORM_PACK16(3, VK10.VK_FORMAT_R5G6B5_UNORM_PACK16, CompatibilityClass.BIT16, 3),
    B5G6R5_UNORM_PACK16(4, VK10.VK_FORMAT_B5G6R5_UNORM_PACK16, CompatibilityClass.BIT16, 3),
    R5G5B5A1_UNORM_PACK16(5, VK10.VK_FORMAT_R5G5B5A1_UNORM_PACK16, CompatibilityClass.BIT16, 4),
    B5G5R5A1_UNORM_PACK16(6, VK10.VK_FORMAT_B5G5R5A1_UNORM_PACK16, CompatibilityClass.BIT16, 4),
    A1R5G5B5_UNORM_PACK16(7, VK10.VK_FORMAT_A1R5G5B5_UNORM_PACK16, CompatibilityClass.BIT16, 4),
    R8_UNORM(8, VK10.VK_FORMAT_R8_UNORM, CompatibilityClass.BIT8, 1),
    R8_SNORM(9, VK10.VK_FORMAT_R8_SNORM, CompatibilityClass.BIT8, 1),
    R8_USCALED(10, VK10.VK_FORMAT_R8_USCALED, CompatibilityClass.BIT8, 1),
    R8_SSCALED(11, VK10.VK_FORMAT_R8_SSCALED, CompatibilityClass.BIT8, 1),
    R8_UINT(12, VK10.VK_FORMAT_R8_UINT, CompatibilityClass.BIT8, 1),
    R8_SINT(13, VK10.VK_FORMAT_R8_SINT, CompatibilityClass.BIT8, 1),
    R8_SRGB(14, VK10.VK_FORMAT_R8_SRGB, CompatibilityClass.BIT8, 1),
    R8G8_UNORM(15, VK10.VK_FORMAT_R8G8_UNORM, CompatibilityClass.BIT16, 2),
    R8G8_SNORM(16, VK10.VK_FORMAT_R8G8_SNORM, CompatibilityClass.BIT16, 2),
    R8G8_USCALED(17, VK10.VK_FORMAT_R8G8_USCALED, CompatibilityClass.BIT16, 2),
    R8G8_SSCALED(18, VK10.VK_FORMAT_R8G8_SSCALED, CompatibilityClass.BIT16, 2),
    R8G8_UINT(19, VK10.VK_FORMAT_R8G8_UINT, CompatibilityClass.BIT16, 2),
    R8G8_SINT(20, VK10.VK_FORMAT_R8G8_SINT, CompatibilityClass.BIT16, 2),
    R8G8_SRGB(21, VK10.VK_FORMAT_R8G8_SRGB, CompatibilityClass.BIT16, 2),
    R8G8B8_UNORM(22, VK10.VK_FORMAT_R8G8B8_UNORM, CompatibilityClass.BIT24, 3),
    R8G8B8_SNORM(23, VK10.VK_FORMAT_R8G8B8_SNORM, CompatibilityClass.BIT24, 3),
    R8G8B8_USCALED(24, VK10.VK_FORMAT_R8G8B8_USCALED, CompatibilityClass.BIT24, 3),
    R8G8B8_SSCALED(25, VK10.VK_FORMAT_R8G8B8_SSCALED, CompatibilityClass.BIT24, 3),
    R8G8B8_UINT(26, VK10.VK_FORMAT_R8G8B8_UINT, CompatibilityClass.BIT24, 3),
    R8G8B8_SINT(27, VK10.VK_FORMAT_R8G8B8_SINT, CompatibilityClass.BIT24, 3),
    R8G8B8_SRGB(28, VK10.VK_FORMAT_R8G8B8_SRGB, CompatibilityClass.BIT24, 3),
    B8G8R8_UNORM(29, VK10.VK_FORMAT_B8G8R8_UNORM, CompatibilityClass.BIT24, 3),
    B8G8R8_SNORM(30, VK10.VK_FORMAT_B8G8R8_SNORM, CompatibilityClass.BIT24, 3),
    B8G8R8_USCALED(31, VK10.VK_FORMAT_B8G8R8_USCALED, CompatibilityClass.BIT24, 3),
    B8G8R8_SSCALED(32, VK10.VK_FORMAT_B8G8R8_SSCALED, CompatibilityClass.BIT24, 3),
    B8G8R8_UINT(33, VK10.VK_FORMAT_B8G8R8_UINT, CompatibilityClass.BIT24, 3),
    B8G8R8_SINT(34, VK10.VK_FORMAT_B8G8R8_SINT, CompatibilityClass.BIT24, 3),
    B8G8R8_SRGB(35, VK10.VK_FORMAT_B8G8R8_SRGB, CompatibilityClass.BIT24, 3),
    R8G8B8A8_UNORM(36, VK10.VK_FORMAT_R8G8B8A8_UNORM, CompatibilityClass.BIT32, 4),
    R8G8B8A8_SNORM(37, VK10.VK_FORMAT_R8G8B8A8_SNORM, CompatibilityClass.BIT32, 4),
    R8G8B8A8_USCALED(38, VK10.VK_FORMAT_R8G8B8A8_USCALED, CompatibilityClass.BIT32, 4),
    R8G8B8A8_SSCALED(39, VK10.VK_FORMAT_R8G8B8A8_SSCALED, CompatibilityClass.BIT32, 4),
    R8G8B8A8_UINT(40, VK10.VK_FORMAT_R8G8B8A8_UINT, CompatibilityClass.BIT32, 4),
    R8G8B8A8_SINT(41, VK10.VK_FORMAT_R8G8B8A8_SINT, CompatibilityClass.BIT32, 4),
    R8G8B8A8_SRGB(42, VK10.VK_FORMAT_R8G8B8A8_SRGB, CompatibilityClass.BIT32, 4),
    B8G8R8A8_UNORM(43, VK10.VK_FORMAT_B8G8R8A8_UNORM, CompatibilityClass.BIT32, 4),
    B8G8R8A8_SNORM(44, VK10.VK_FORMAT_B8G8R8A8_SNORM, CompatibilityClass.BIT32, 4),
    B8G8R8A8_USCALED(45, VK10.VK_FORMAT_B8G8R8A8_USCALED, CompatibilityClass.BIT32, 4),
    B8G8R8A8_SSCALED(46, VK10.VK_FORMAT_B8G8R8A8_SSCALED, CompatibilityClass.BIT32, 4),
    B8G8R8A8_UINT(47, VK10.VK_FORMAT_B8G8R8A8_UINT, CompatibilityClass.BIT32, 4),
    B8G8R8A8_SINT(48, VK10.VK_FORMAT_B8G8R8A8_SINT, CompatibilityClass.BIT32, 4),
    B8G8R8A8_SRGB(49, VK10.VK_FORMAT_B8G8R8A8_SRGB, CompatibilityClass.BIT32, 4),
    A8B8G8R8_UNORM_PACK32(50, VK10.VK_FORMAT_A8B8G8R8_UNORM_PACK32, CompatibilityClass.BIT32, 4),
    A8B8G8R8_SNORM_PACK32(51, VK10.VK_FORMAT_A8B8G8R8_SNORM_PACK32, CompatibilityClass.BIT32, 4),
    A8B8G8R8_USCALED_PACK32(52, VK10.VK_FORMAT_A8B8G8R8_USCALED_PACK32, CompatibilityClass.BIT32, 4),
    A8B8G8R8_SSCALED_PACK32(53, VK10.VK_FORMAT_A8B8G8R8_SSCALED_PACK32, CompatibilityClass.BIT32, 4),
    A8B8G8R8_UINT_PACK32(54, VK10.VK_FORMAT_A8B8G8R8_UINT_PACK32, CompatibilityClass.BIT32, 4),
    A8B8G8R8_SINT_PACK32(55, VK10.VK_FORMAT_A8B8G8R8_SINT_PACK32, CompatibilityClass.BIT32, 4),
    A8B8G8R8_SRGB_PACK32(56, VK10.VK_FORMAT_A8B8G8R8_SRGB_PACK32, CompatibilityClass.BIT32, 4),
    A2R10G10B10_UNORM_PACK32(57, VK10.VK_FORMAT_A2R10G10B10_UNORM_PACK32, CompatibilityClass.BIT32, 4),
    A2R10G10B10_SNORM_PACK32(58, VK10.VK_FORMAT_A2R10G10B10_SNORM_PACK32, CompatibilityClass.BIT32, 4),
    A2R10G10B10_USCALED_PACK32(59, VK10.VK_FORMAT_A2R10G10B10_USCALED_PACK32, CompatibilityClass.BIT32, 4),
    A2R10G10B10_SSCALED_PACK32(60, VK10.VK_FORMAT_A2R10G10B10_SSCALED_PACK32, CompatibilityClass.BIT32, 4),
    A2R10G10B10_UINT_PACK32(61, VK10.VK_FORMAT_A2R10G10B10_UINT_PACK32, CompatibilityClass.BIT32, 4),
    A2R10G10B10_SINT_PACK32(62, VK10.VK_FORMAT_A2R10G10B10_SINT_PACK32, CompatibilityClass.BIT32, 4),
    A2B10G10R10_UNORM_PACK32(63, VK10.VK_FORMAT_A2B10G10R10_UNORM_PACK32, CompatibilityClass.BIT32, 4),
    A2B10G10R10_SNORM_PACK32(64, VK10.VK_FORMAT_A2B10G10R10_SNORM_PACK32, CompatibilityClass.BIT32, 4),
    A2B10G10R10_USCALED_PACK32(65, VK10.VK_FORMAT_A2B10G10R10_USCALED_PACK32, CompatibilityClass.BIT32, 4),
    A2B10G10R10_SSCALED_PACK32(66, VK10.VK_FORMAT_A2B10G10R10_SSCALED_PACK32, CompatibilityClass.BIT32, 4),
    A2B10G10R10_UINT_PACK32(67, VK10.VK_FORMAT_A2B10G10R10_UINT_PACK32, CompatibilityClass.BIT32, 4),
    A2B10G10R10_SINT_PACK32(68, VK10.VK_FORMAT_A2B10G10R10_SINT_PACK32, CompatibilityClass.BIT32, 4),
    R16_UNORM(69, VK10.VK_FORMAT_R16_UNORM, CompatibilityClass.BIT16, 1),
    R16_SNORM(70, VK10.VK_FORMAT_R16_SNORM, CompatibilityClass.BIT16, 1),
    R16_USCALED(71, VK10.VK_FORMAT_R16_USCALED, CompatibilityClass.BIT16, 1),
    R16_SSCALED(72, VK10.VK_FORMAT_R16_SSCALED, CompatibilityClass.BIT16, 1),
    R16_UINT(73, VK10.VK_FORMAT_R16_UINT, CompatibilityClass.BIT16, 1),
    R16_SINT(74, VK10.VK_FORMAT_R16_SINT, CompatibilityClass.BIT16, 1),
    R16_SFLOAT(75, VK10.VK_FORMAT_R16_SFLOAT, CompatibilityClass.BIT16, 1),
    R16G16_UNORM(76, VK10.VK_FORMAT_R16G16_UNORM, CompatibilityClass.BIT32, 2),
    R16G16_SNORM(77, VK10.VK_FORMAT_R16G16_SNORM, CompatibilityClass.BIT32, 2),
    R16G16_USCALED(78, VK10.VK_FORMAT_R16G16_USCALED, CompatibilityClass.BIT32, 2),
    R16G16_SSCALED(79, VK10.VK_FORMAT_R16G16_SSCALED, CompatibilityClass.BIT32, 2),
    R16G16_UINT(80, VK10.VK_FORMAT_R16G16_UINT, CompatibilityClass.BIT32, 2),
    R16G16_SINT(81, VK10.VK_FORMAT_R16G16_SINT, CompatibilityClass.BIT32, 2),
    R16G16_SFLOAT(82, VK10.VK_FORMAT_R16G16_SFLOAT, CompatibilityClass.BIT32, 2),
    R16G16B16_UNORM(83, VK10.VK_FORMAT_R16G16B16_UNORM, CompatibilityClass.BIT48, 3),
    R16G16B16_SNORM(84, VK10.VK_FORMAT_R16G16B16_SNORM, CompatibilityClass.BIT48, 3),
    R16G16B16_USCALED(85, VK10.VK_FORMAT_R16G16B16_USCALED, CompatibilityClass.BIT48, 3),
    R16G16B16_SSCALED(86, VK10.VK_FORMAT_R16G16B16_SSCALED, CompatibilityClass.BIT48, 3),
    R16G16B16_UINT(87, VK10.VK_FORMAT_R16G16B16_UINT, CompatibilityClass.BIT48, 3),
    R16G16B16_SINT(88, VK10.VK_FORMAT_R16G16B16_SINT, CompatibilityClass.BIT48, 3),
    R16G16B16_SFLOAT(89, VK10.VK_FORMAT_R16G16B16_SFLOAT, CompatibilityClass.BIT48, 3),
    R16G16B16A16_UNORM(90, VK10.VK_FORMAT_R16G16B16A16_UNORM, CompatibilityClass.BIT64, 4),
    R16G16B16A16_SNORM(91, VK10.VK_FORMAT_R16G16B16A16_SNORM, CompatibilityClass.BIT64, 4),
    R16G16B16A16_USCALED(92, VK10.VK_FORMAT_R16G16B16A16_USCALED, CompatibilityClass.BIT64, 4),
    R16G16B16A16_SSCALED(93, VK10.VK_FORMAT_R16G16B16A16_SSCALED, CompatibilityClass.BIT64, 4),
    R16G16B16A16_UINT(94, VK10.VK_FORMAT_R16G16B16A16_UINT, CompatibilityClass.BIT64, 4),
    R16G16B16A16_SINT(95, VK10.VK_FORMAT_R16G16B16A16_SINT, CompatibilityClass.BIT64, 4),
    R16G16B16A16_SFLOAT(96, VK10.VK_FORMAT_R16G16B16A16_SFLOAT, CompatibilityClass.BIT64, 4),
    R32_UINT(97, VK10.VK_FORMAT_R32_UINT, CompatibilityClass.BIT32, 1),
    R32_SINT(98, VK10.VK_FORMAT_R32_SINT, CompatibilityClass.BIT32, 1),
    R32_SFLOAT(99, VK10.VK_FORMAT_R32_SFLOAT, CompatibilityClass.BIT32, 1),
    R32G32_UINT(100, VK10.VK_FORMAT_R32G32_UINT, CompatibilityClass.BIT64, 2),
    R32G32_SINT(101, VK10.VK_FORMAT_R32G32_SINT, CompatibilityClass.BIT64, 2),
    R32G32_SFLOAT(102, VK10.VK_FORMAT_R32G32_SFLOAT, CompatibilityClass.BIT64, 2),
    R32G32B32_UINT(103, VK10.VK_FORMAT_R32G32B32_UINT, CompatibilityClass.BIT96, 3),
    R32G32B32_SINT(104, VK10.VK_FORMAT_R32G32B32_SINT, CompatibilityClass.BIT96, 3),
    R32G32B32_SFLOAT(105, VK10.VK_FORMAT_R32G32B32_SFLOAT, CompatibilityClass.BIT96, 3),
    R32G32B32A32_UINT(106, VK10.VK_FORMAT_R32G32B32A32_UINT, CompatibilityClass.BIT128, 4),
    R32G32B32A32_SINT(107, VK10.VK_FORMAT_R32G32B32A32_SINT, CompatibilityClass.BIT128, 4),
    R32G32B32A32_SFLOAT(108, VK10.VK_FORMAT_R32G32B32A32_SFLOAT, CompatibilityClass.BIT128, 4),
    R64_UINT(109, VK10.VK_FORMAT_R64_UINT, CompatibilityClass.BIT64, 1),
    R64_SINT(110, VK10.VK_FORMAT_R64_SINT, CompatibilityClass.BIT64, 1),
    R64_SFLOAT(111, VK10.VK_FORMAT_R64_SFLOAT, CompatibilityClass.BIT64, 1),
    R64G64_UINT(112, VK10.VK_FORMAT_R64G64_UINT, CompatibilityClass.BIT128, 2),
    R64G64_SINT(113, VK10.VK_FORMAT_R64G64_SINT, CompatibilityClass.BIT128, 2),
    R64G64_SFLOAT(114, VK10.VK_FORMAT_R64G64_SFLOAT, CompatibilityClass.BIT128, 2),
    R64G64B64_UINT(115, VK10.VK_FORMAT_R64G64B64_UINT, CompatibilityClass.BIT192, 3),
    R64G64B64_SINT(116, VK10.VK_FORMAT_R64G64B64_SINT, CompatibilityClass.BIT192, 3),
    R64G64B64_SFLOAT(117, VK10.VK_FORMAT_R64G64B64_SFLOAT, CompatibilityClass.BIT192, 3),
    R64G64B64A64_UINT(118, VK10.VK_FORMAT_R64G64B64A64_UINT, CompatibilityClass.BIT256, 4),
    R64G64B64A64_SINT(119, VK10.VK_FORMAT_R64G64B64A64_SINT, CompatibilityClass.BIT256, 4),
    R64G64B64A64_SFLOAT(120, VK10.VK_FORMAT_R64G64B64A64_SFLOAT, CompatibilityClass.BIT256, 4),
    B10G11R11_UFLOAT_PACK32(121, VK10.VK_FORMAT_B10G11R11_UFLOAT_PACK32, CompatibilityClass.BIT32, 3),
    E5B9G9R9_UFLOAT_PACK32(122, VK10.VK_FORMAT_E5B9G9R9_UFLOAT_PACK32, CompatibilityClass.BIT32, 3),
    D16_UNORM(123, VK10.VK_FORMAT_D16_UNORM, CompatibilityClass.D16, 1),
    X8_D24_UNORM_PACK32(124, VK10.VK_FORMAT_X8_D24_UNORM_PACK32, CompatibilityClass.D24, 1),
    D32_SFLOAT(125, VK10.VK_FORMAT_D32_SFLOAT, CompatibilityClass.D32, 1),
    S8_UINT(126, VK10.VK_FORMAT_S8_UINT, CompatibilityClass.S8, 1),
    D16_UNORM_S8_UINT(127, VK10.VK_FORMAT_D16_UNORM_S8_UINT, CompatibilityClass.D16S8, 2),
    D24_UNORM_S8_UINT(128, VK10.VK_FORMAT_D24_UNORM_S8_UINT, CompatibilityClass.D24S8, 2),
    D32_SFLOAT_S8_UINT(129, VK10.VK_FORMAT_D32_SFLOAT_S8_UINT, CompatibilityClass.D32S8, 2),
    BC1_RGB_UNORM_BLOCK(130, VK10.VK_FORMAT_BC1_RGB_UNORM_BLOCK, CompatibilityClass.BC1_RGB, 3),
    BC1_RGB_SRGB_BLOCK(131, VK10.VK_FORMAT_BC1_RGB_SRGB_BLOCK, CompatibilityClass.BC1_RGB, 3),
    BC1_RGBA_UNORM_BLOCK(132, VK10.VK_FORMAT_BC1_RGBA_UNORM_BLOCK, CompatibilityClass.BC1_RGBA, 4),
    BC1_RGBA_SRGB_BLOCK(133, VK10.VK_FORMAT_BC1_RGBA_SRGB_BLOCK, CompatibilityClass.BC1_RGBA, 4),
    BC2_UNORM_BLOCK(134, VK10.VK_FORMAT_BC2_UNORM_BLOCK, CompatibilityClass.BC2, 4),
    BC2_SRGB_BLOCK(135, VK10.VK_FORMAT_BC2_SRGB_BLOCK, CompatibilityClass.BC2, 4),
    BC3_UNORM_BLOCK(136, VK10.VK_FORMAT_BC3_UNORM_BLOCK, CompatibilityClass.BC3, 4),
    BC3_SRGB_BLOCK(137, VK10.VK_FORMAT_BC3_SRGB_BLOCK, CompatibilityClass.BC3, 4),
    BC4_UNORM_BLOCK(138, VK10.VK_FORMAT_BC4_UNORM_BLOCK, CompatibilityClass.BC4, 1),
    BC4_SNORM_BLOCK(139, VK10.VK_FORMAT_BC4_SNORM_BLOCK, CompatibilityClass.BC4, 1),
    BC5_UNORM_BLOCK(140, VK10.VK_FORMAT_BC5_UNORM_BLOCK, CompatibilityClass.BC5, 2),
    BC5_SNORM_BLOCK(141, VK10.VK_FORMAT_BC5_SNORM_BLOCK, CompatibilityClass.BC5, 2),
    BC6H_UFLOAT_BLOCK(142, VK10.VK_FORMAT_BC6H_UFLOAT_BLOCK, CompatibilityClass.BC6H, 3),
    BC6H_SFLOAT_BLOCK(143, VK10.VK_FORMAT_BC6H_SFLOAT_BLOCK, CompatibilityClass.BC6H, 3),
    BC7_UNORM_BLOCK(144, VK10.VK_FORMAT_BC7_UNORM_BLOCK, CompatibilityClass.BC7, 4),
    BC7_SRGB_BLOCK(145, VK10.VK_FORMAT_BC7_SRGB_BLOCK, CompatibilityClass.BC7, 4),
    ETC2_R8G8B8_UNORM_BLOCK(146, VK10.VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK, CompatibilityClass.ETC2_RGB, 3),
    ETC2_R8G8B8_SRGB_BLOCK(147, VK10.VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK, CompatibilityClass.ETC2_RGB, 3),
    ETC2_R8G8B8A1_UNORM_BLOCK(148, VK10.VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK, CompatibilityClass.ETC2_RGBA, 4),
    ETC2_R8G8B8A1_SRGB_BLOCK(149, VK10.VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK, CompatibilityClass.ETC2_RGBA, 4),
    ETC2_R8G8B8A8_UNORM_BLOCK(150, VK10.VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK, CompatibilityClass.ETC2_EAC_RGBA, 4),
    ETC2_R8G8B8A8_SRGB_BLOCK(151, VK10.VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK, CompatibilityClass.ETC2_EAC_RGBA, 4),
    EAC_R11_UNORM_BLOCK(152, VK10.VK_FORMAT_EAC_R11_UNORM_BLOCK, CompatibilityClass.EAC_R, 1),
    EAC_R11_SNORM_BLOCK(153, VK10.VK_FORMAT_EAC_R11_SNORM_BLOCK, CompatibilityClass.EAC_R, 1),
    EAC_R11G11_UNORM_BLOCK(154, VK10.VK_FORMAT_EAC_R11G11_UNORM_BLOCK, CompatibilityClass.EAC_RG, 2),
    EAC_R11G11_SNORM_BLOCK(155, VK10.VK_FORMAT_EAC_R11G11_SNORM_BLOCK, CompatibilityClass.EAC_RG, 2),
    ASTC_4x4_UNORM_BLOCK(156, VK10.VK_FORMAT_ASTC_4x4_UNORM_BLOCK, CompatibilityClass.ASTC_4x4, 4),
    ASTC_4x4_SRGB_BLOCK(157, VK10.VK_FORMAT_ASTC_4x4_SRGB_BLOCK, CompatibilityClass.ASTC_4x4, 4),
    ASTC_5x4_UNORM_BLOCK(158, VK10.VK_FORMAT_ASTC_5x4_UNORM_BLOCK, CompatibilityClass.ASTC_5x4, 4),
    ASTC_5x4_SRGB_BLOCK(159, VK10.VK_FORMAT_ASTC_5x4_SRGB_BLOCK, CompatibilityClass.ASTC_5x4, 4),
    ASTC_5x5_UNORM_BLOCK(160, VK10.VK_FORMAT_ASTC_5x5_UNORM_BLOCK, CompatibilityClass.ASTC_5x5, 4),
    ASTC_5x5_SRGB_BLOCK(161, VK10.VK_FORMAT_ASTC_5x5_SRGB_BLOCK, CompatibilityClass.ASTC_5x5, 4),
    ASTC_6x5_UNORM_BLOCK(162, VK10.VK_FORMAT_ASTC_6x5_UNORM_BLOCK, CompatibilityClass.ASTC_6x5, 4),
    ASTC_6x5_SRGB_BLOCK(163, VK10.VK_FORMAT_ASTC_6x5_SRGB_BLOCK, CompatibilityClass.ASTC_6x5, 4),
    ASTC_6x6_UNORM_BLOCK(164, VK10.VK_FORMAT_ASTC_6x6_UNORM_BLOCK, CompatibilityClass.ASTC_6x6, 4),
    ASTC_6x6_SRGB_BLOCK(165, VK10.VK_FORMAT_ASTC_6x6_SRGB_BLOCK, CompatibilityClass.ASTC_6x6, 4),
    ASTC_8x5_UNORM_BLOCK(166, VK10.VK_FORMAT_ASTC_8x5_UNORM_BLOCK, CompatibilityClass.ASTC_8x5, 4),
    ASTC_8x5_SRGB_BLOCK(167, VK10.VK_FORMAT_ASTC_8x5_SRGB_BLOCK, CompatibilityClass.ASTC_8x5, 4),
    ASTC_8x6_UNORM_BLOCK(168, VK10.VK_FORMAT_ASTC_8x6_UNORM_BLOCK, CompatibilityClass.ASTC_8x6, 4),
    ASTC_8x6_SRGB_BLOCK(169, VK10.VK_FORMAT_ASTC_8x6_SRGB_BLOCK, CompatibilityClass.ASTC_8x6, 4),
    ASTC_8x8_UNORM_BLOCK(170, VK10.VK_FORMAT_ASTC_8x8_UNORM_BLOCK, CompatibilityClass.ASTC_8x8, 4),
    ASTC_8x8_SRGB_BLOCK(171, VK10.VK_FORMAT_ASTC_8x8_SRGB_BLOCK, CompatibilityClass.ASTC_8x8, 4),
    ASTC_10x5_UNORM_BLOCK(172, VK10.VK_FORMAT_ASTC_10x5_UNORM_BLOCK, CompatibilityClass.ASTC_10x5, 4),
    ASTC_10x5_SRGB_BLOCK(173, VK10.VK_FORMAT_ASTC_10x5_SRGB_BLOCK, CompatibilityClass.ASTC_10x5, 4),
    ASTC_10x6_UNORM_BLOCK(174, VK10.VK_FORMAT_ASTC_10x6_UNORM_BLOCK, CompatibilityClass.ASTC_10x6, 4),
    ASTC_10x6_SRGB_BLOCK(175, VK10.VK_FORMAT_ASTC_10x6_SRGB_BLOCK, CompatibilityClass.ASTC_10x6, 4),
    ASTC_10x8_UNORM_BLOCK(176, VK10.VK_FORMAT_ASTC_10x8_UNORM_BLOCK, CompatibilityClass.ASTC_10x8, 4),
    ASTC_10x8_SRGB_BLOCK(177, VK10.VK_FORMAT_ASTC_10x8_SRGB_BLOCK, CompatibilityClass.ASTC_10x8, 4),
    ASTC_10x10_UNORM_BLOCK(178, VK10.VK_FORMAT_ASTC_10x10_UNORM_BLOCK, CompatibilityClass.ASTC_10x10, 4),
    ASTC_10x10_SRGB_BLOCK(179, VK10.VK_FORMAT_ASTC_10x10_SRGB_BLOCK, CompatibilityClass.ASTC_10x10, 4),
    ASTC_12x10_UNORM_BLOCK(180, VK10.VK_FORMAT_ASTC_12x10_UNORM_BLOCK, CompatibilityClass.ASTC_12x10, 4),
    ASTC_12x10_SRGB_BLOCK(181, VK10.VK_FORMAT_ASTC_12x10_SRGB_BLOCK, CompatibilityClass.ASTC_12x10, 4),
    ASTC_12x12_UNORM_BLOCK(182, VK10.VK_FORMAT_ASTC_12x12_UNORM_BLOCK, CompatibilityClass.ASTC_12x12, 4),
    ASTC_12x12_SRGB_BLOCK(183, VK10.VK_FORMAT_ASTC_12x12_SRGB_BLOCK, CompatibilityClass.ASTC_12x12, 4),
    G8B8G8R8_422_UNORM(184, VK11.VK_FORMAT_G8B8G8R8_422_UNORM, CompatibilityClass.BIT32_G8B8G8R8, 4),
    B8G8R8G8_422_UNORM(185, VK11.VK_FORMAT_B8G8R8G8_422_UNORM, CompatibilityClass.BIT32_B8G8R8G8, 4),
    G8_B8_R8_3PLANE_420_UNORM(186, VK11.VK_FORMAT_G8_B8_R8_3PLANE_420_UNORM, CompatibilityClass.PLANE3_8BIT_420, 3),
    G8_B8R8_2PLANE_420_UNORM(187, VK11.VK_FORMAT_G8_B8R8_2PLANE_420_UNORM, CompatibilityClass.PLANE2_8BIT_420, 3),
    G8_B8_R8_3PLANE_422_UNORM(188, VK11.VK_FORMAT_G8_B8_R8_3PLANE_422_UNORM, CompatibilityClass.PLANE3_8BIT_422, 3),
    G8_B8R8_2PLANE_422_UNORM(189, VK11.VK_FORMAT_G8_B8R8_2PLANE_422_UNORM, CompatibilityClass.PLANE2_8BIT_422, 3),
    G8_B8_R8_3PLANE_444_UNORM(190, VK11.VK_FORMAT_G8_B8_R8_3PLANE_444_UNORM, CompatibilityClass.PLANE3_8BIT_444, 3),
    R10X6_UNORM_PACK16(191, VK11.VK_FORMAT_R10X6_UNORM_PACK16, CompatibilityClass.BIT16, 1),
    R10X6G10X6_UNORM_2PACK16(192, VK11.VK_FORMAT_R10X6G10X6_UNORM_2PACK16, CompatibilityClass.BIT32, 2),
    R10X6G10X6B10X6A10X6_UNORM_4PACK16(193, VK11.VK_FORMAT_R10X6G10X6B10X6A10X6_UNORM_4PACK16, CompatibilityClass.BIT64_R10G10B10A10, 4),
    G10X6B10X6G10X6R10X6_422_UNORM_4PACK16(194, VK11.VK_FORMAT_G10X6B10X6G10X6R10X6_422_UNORM_4PACK16, CompatibilityClass.BIT64_G10B10G10R10, 4),
    B10X6G10X6R10X6G10X6_422_UNORM_4PACK16(195, VK11.VK_FORMAT_B10X6G10X6R10X6G10X6_422_UNORM_4PACK16, CompatibilityClass.BIT64_B10G10R10G10, 4),
    G10X6_B10X6_R10X6_3PLANE_420_UNORM_3PACK16(196, VK11.VK_FORMAT_G10X6_B10X6_R10X6_3PLANE_420_UNORM_3PACK16, CompatibilityClass.PLANE3_10BIT_420, 3),
    G10X6_B10X6R10X6_2PLANE_420_UNORM_3PACK16(197, VK11.VK_FORMAT_G10X6_B10X6R10X6_2PLANE_420_UNORM_3PACK16, CompatibilityClass.PLANE2_10BIT_420, 3),
    G10X6_B10X6_R10X6_3PLANE_422_UNORM_3PACK16(198, VK11.VK_FORMAT_G10X6_B10X6_R10X6_3PLANE_422_UNORM_3PACK16, CompatibilityClass.PLANE3_10BIT_422, 3),
    G10X6_B10X6R10X6_2PLANE_422_UNORM_3PACK16(199, VK11.VK_FORMAT_G10X6_B10X6R10X6_2PLANE_422_UNORM_3PACK16, CompatibilityClass.PLANE2_10BIT_422, 3),
    G10X6_B10X6_R10X6_3PLANE_444_UNORM_3PACK16(200, VK11.VK_FORMAT_G10X6_B10X6_R10X6_3PLANE_444_UNORM_3PACK16, CompatibilityClass.PLANE3_10BIT_444, 3),
    R12X4_UNORM_PACK16(201, VK11.VK_FORMAT_R12X4_UNORM_PACK16, CompatibilityClass.BIT16, 1),
    R12X4G12X4_UNORM_2PACK16(202, VK11.VK_FORMAT_R12X4G12X4_UNORM_2PACK16, CompatibilityClass.BIT32, 2),
    R12X4G12X4B12X4A12X4_UNORM_4PACK16(203, VK11.VK_FORMAT_R12X4G12X4B12X4A12X4_UNORM_4PACK16, CompatibilityClass.BIT64_R12G12B12A12, 4),
    G12X4B12X4G12X4R12X4_422_UNORM_4PACK16(204, VK11.VK_FORMAT_G12X4B12X4G12X4R12X4_422_UNORM_4PACK16, CompatibilityClass.BIT64_G12B12G12R12, 4),
    B12X4G12X4R12X4G12X4_422_UNORM_4PACK16(205, VK11.VK_FORMAT_B12X4G12X4R12X4G12X4_422_UNORM_4PACK16, CompatibilityClass.BIT64_B12G12R12G12, 4),
    G12X4_B12X4_R12X4_3PLANE_420_UNORM_3PACK16(206, VK11.VK_FORMAT_G12X4_B12X4_R12X4_3PLANE_420_UNORM_3PACK16, CompatibilityClass.PLANE3_12BIT_420, 3),
    G12X4_B12X4R12X4_2PLANE_420_UNORM_3PACK16(207, VK11.VK_FORMAT_G12X4_B12X4R12X4_2PLANE_420_UNORM_3PACK16, CompatibilityClass.PLANE2_12BIT_420, 3),
    G12X4_B12X4_R12X4_3PLANE_422_UNORM_3PACK16(208, VK11.VK_FORMAT_G12X4_B12X4_R12X4_3PLANE_422_UNORM_3PACK16, CompatibilityClass.PLANE3_12BIT_422, 3),
    G12X4_B12X4R12X4_2PLANE_422_UNORM_3PACK16(209, VK11.VK_FORMAT_G12X4_B12X4R12X4_2PLANE_422_UNORM_3PACK16, CompatibilityClass.PLANE2_12BIT_422, 3),
    G12X4_B12X4_R12X4_3PLANE_444_UNORM_3PACK16(210, VK11.VK_FORMAT_G12X4_B12X4_R12X4_3PLANE_444_UNORM_3PACK16, CompatibilityClass.PLANE3_12BIT_444, 3),
    G16B16G16R16_422_UNORM(211, VK11.VK_FORMAT_G16B16G16R16_422_UNORM, CompatibilityClass.BIT64_G16B16G16R16, 3),
    B16G16R16G16_422_UNORM(212, VK11.VK_FORMAT_B16G16R16G16_422_UNORM, CompatibilityClass.BIT64_B16G16R16G16, 3),
    G16_B16_R16_3PLANE_420_UNORM(213, VK11.VK_FORMAT_G16_B16_R16_3PLANE_420_UNORM, CompatibilityClass.PLANE3_16BIT_420, 3),
    G16_B16R16_2PLANE_420_UNORM(214, VK11.VK_FORMAT_G16_B16R16_2PLANE_420_UNORM, CompatibilityClass.PLANE2_16BIT_420, 3),
    G16_B16_R16_3PLANE_422_UNORM(215, VK11.VK_FORMAT_G16_B16_R16_3PLANE_422_UNORM, CompatibilityClass.PLANE3_16BIT_422, 3),
    G16_B16R16_2PLANE_422_UNORM(216, VK11.VK_FORMAT_G16_B16R16_2PLANE_422_UNORM, CompatibilityClass.PLANE2_16BIT_422, 3),
    G16_B16_R16_3PLANE_444_UNORM(217, VK11.VK_FORMAT_G16_B16_R16_3PLANE_444_UNORM, CompatibilityClass.PLANE3_16BIT_444, 3);

    /**
     * The highest id of any format.
     */
    public static final int maxId = 217;

    public final int id;
    public final int vulkan;

    public final CompatibilityClass compatibilityClass;

    public final int channelCount;

    ImageFormat(int id, int vulkan, CompatibilityClass compatibilityClass, int channelCount) {
        this.id = id;
        this.vulkan = vulkan;
        this.compatibilityClass = compatibilityClass;
        this.channelCount = channelCount;
    }

    /**
     * @param other The format to compare against
     * @return True if the 2 formats are in the same compatibility class as specified by the vulkan spec.
     */
    public boolean isCompatibleWith(ImageFormat other) {
        return this.compatibilityClass == other.compatibilityClass;
    }

    public enum CompatibilityClass {
        BIT8,
        BIT16,
        BIT24,
        BIT32,
        BIT32_G8B8G8R8,
        BIT32_B8G8R8G8,
        BIT48,
        BIT64,
        BIT64_R10G10B10A10,
        BIT64_G10B10G10R10,
        BIT64_B10G10R10G10,
        BIT64_R12G12B12A12,
        BIT64_G12B12G12R12,
        BIT64_B12G12R12G12,
        BIT64_G16B16G16R16,
        BIT64_B16G16R16G16,
        BIT96,
        BIT128,
        BIT192,
        BIT256,
        BC1_RGB,
        BC1_RGBA,
        BC2,
        BC3,
        BC4,
        BC5,
        BC6H,
        BC7,
        ETC2_RGB,
        ETC2_RGBA,
        ETC2_EAC_RGBA,
        EAC_R,
        EAC_RG,
        ASTC_4x4,
        ASTC_5x4,
        ASTC_5x5,
        ASTC_6x5,
        ASTC_6x6,
        ASTC_8x5,
        ASTC_8x6,
        ASTC_8x8,
        ASTC_10x5,
        ASTC_10x6,
        ASTC_10x8,
        ASTC_10x10,
        ASTC_12x10,
        ASTC_12x12,
        D16,
        D24,
        D32,
        S8,
        D16S8,
        D24S8,
        D32S8,
        PLANE3_8BIT_420,
        PLANE2_8BIT_420,
        PLANE3_8BIT_422,
        PLANE2_8BIT_422,
        PLANE3_8BIT_444,
        PLANE3_10BIT_420,
        PLANE2_10BIT_420,
        PLANE3_10BIT_422,
        PLANE2_10BIT_422,
        PLANE3_10BIT_444,
        PLANE3_12BIT_420,
        PLANE2_12BIT_420,
        PLANE3_12BIT_422,
        PLANE2_12BIT_422,
        PLANE3_12BIT_444,
        PLANE3_16BIT_420,
        PLANE2_16BIT_420,
        PLANE3_16BIT_422,
        PLANE2_16BIT_422,
        PLANE3_16BIT_444
    }
}