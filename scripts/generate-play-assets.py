#!/usr/bin/env python3

from __future__ import annotations

from pathlib import Path
from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parent.parent
OUTPUT = ROOT / "play-store" / "assets" / "generated"
OUTPUT.mkdir(parents=True, exist_ok=True)

BG = "#FFF8F1"
PRIMARY = "#204B57"
SECONDARY = "#E08A45"
TEXT = "#1F2933"
SURFACE = "#FFFFFF"


def font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    candidates = [
        "/System/Library/Fonts/Supplemental/Arial Unicode.ttf",
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf" if bold else "/System/Library/Fonts/Supplemental/Arial.ttf",
        "/System/Library/Fonts/SFNS.ttf",
    ]
    for candidate in candidates:
        path = Path(candidate)
        if path.exists():
            return ImageFont.truetype(str(path), size=size)
    return ImageFont.load_default()


def rounded_rect(draw: ImageDraw.ImageDraw, box: tuple[int, int, int, int], radius: int, fill: str) -> None:
    draw.rounded_rectangle(box, radius=radius, fill=fill)


def draw_home_preview(draw: ImageDraw.ImageDraw, origin: tuple[int, int], scale: float) -> None:
    ox, oy = origin
    width = int(360 * scale)
    height = int(760 * scale)
    rounded_rect(draw, (ox, oy, ox + width, oy + height), int(44 * scale), SURFACE)
    rounded_rect(draw, (ox + int(24 * scale), oy + int(30 * scale), ox + width - int(24 * scale), oy + int(90 * scale)), int(24 * scale), "#F4E5D5")
    for row in range(2):
        for col in range(2):
            x = ox + int(28 * scale) + col * int(148 * scale)
            y = oy + int(132 * scale) + row * int(148 * scale)
            rounded_rect(draw, (x, y, x + int(124 * scale), y + int(124 * scale)), int(28 * scale), "#F8F0E7")
            draw.text((x + int(18 * scale), y + int(18 * scale)), ["Social", "Work", "Media", "Tools"][row * 2 + col], fill=PRIMARY, font=font(int(16 * scale), bold=True))
    dock_top = oy + height - int(124 * scale)
    rounded_rect(draw, (ox + int(24 * scale), dock_top, ox + width - int(24 * scale), oy + height - int(24 * scale)), int(28 * scale), "#F4E5D5")
    for i in range(4):
        cx = ox + int(58 * scale) + i * int(74 * scale)
        cy = dock_top + int(34 * scale)
        draw.ellipse((cx, cy, cx + int(42 * scale), cy + int(42 * scale)), fill=SECONDARY if i % 2 == 0 else PRIMARY)


def build_feature_graphic() -> None:
    image = Image.new("RGB", (1024, 500), BG)
    draw = ImageDraw.Draw(image)
    draw.rectangle((0, 0, 1024, 500), fill=BG)
    draw.ellipse((-120, -60, 320, 380), fill="#F0D9C4")
    draw.ellipse((760, 200, 1120, 620), fill="#EAD2BF")

    draw.text((72, 76), "Tidy Launcher", fill=PRIMARY, font=font(54, bold=True))
    draw.text((72, 146), "Auto-organize your Android home screen", fill=TEXT, font=font(28, bold=True))
    draw.text((72, 192), "Functional folders. Color folders. 1-page or 2-page layouts.\nEverything stays on-device.", fill=TEXT, font=font(22))

    rounded_rect(draw, (72, 314, 252, 372), 24, PRIMARY)
    draw.text((110, 329), "Local-first", fill="#FFFFFF", font=font(24, bold=True))

    rounded_rect(draw, (272, 314, 504, 372), 24, SECONDARY)
    draw.text((308, 329), "No manual drag", fill="#FFFFFF", font=font(24, bold=True))

    draw_home_preview(draw, (690, 36), 0.54)
    image.save(OUTPUT / "feature-graphic-1024x500.png")


def build_store_icon() -> None:
    image = Image.new("RGB", (512, 512), BG)
    draw = ImageDraw.Draw(image)
    rounded_rect(draw, (32, 32, 480, 480), 120, PRIMARY)
    rounded_rect(draw, (104, 104, 408, 408), 72, "#F5E8DC")
    rounded_rect(draw, (142, 150, 370, 224), 26, SECONDARY)
    rounded_rect(draw, (142, 250, 250, 358), 32, "#F0D9C4")
    rounded_rect(draw, (262, 250, 370, 358), 32, "#EAD2BF")
    draw.text((170, 169), "Tidy", fill="#FFFFFF", font=font(36, bold=True))
    image.save(OUTPUT / "play-icon-512.png")


def build_phone_screenshot(name: str, title: str, body: str, accent: str, mode: str) -> None:
    image = Image.new("RGB", (1080, 1920), BG)
    draw = ImageDraw.Draw(image)
    draw.rectangle((0, 0, 1080, 1920), fill=BG)
    draw.ellipse((-180, -120, 520, 540), fill="#F0D9C4")
    draw.ellipse((760, 1320, 1260, 1960), fill="#EAD2BF")
    draw.text((82, 120), title, fill=PRIMARY, font=font(60, bold=True))
    draw.text((82, 210), body, fill=TEXT, font=font(28))
    rounded_rect(draw, (82, 314, 320, 382), 28, accent)
    draw.text((118, 333), mode, fill="#FFFFFF", font=font(28, bold=True))
    draw_home_preview(draw, (170, 470), 1.8)
    image.save(OUTPUT / name)


def main() -> None:
    build_feature_graphic()
    build_store_icon()
    build_phone_screenshot(
        "screenshot-home-auto-organize.png",
        "Auto organize in one tap",
        "Tidy Launcher recommends a layout and keeps your apps arranged automatically.",
        PRIMARY,
        "Functional mode",
    )
    build_phone_screenshot(
        "screenshot-color-layouts.png",
        "Color folders, calmer home screen",
        "Switch to color mode for a visual layout that still keeps search and the drawer fast.",
        SECONDARY,
        "Color mode",
    )
    build_phone_screenshot(
        "screenshot-privacy-local.png",
        "Private by default",
        "No account, no cloud sync, no analytics. Installed app metadata stays on your device.",
        PRIMARY,
        "Local only",
    )


if __name__ == "__main__":
    main()
