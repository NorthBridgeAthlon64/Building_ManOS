#!/usr/bin/env python3
"""将 docs/course_example_java_learn 下 GBK/GB2312 文本转为 UTF-8。"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "docs" / "course_example_java_learn"

TEXT_EXT = {
    ".java", ".txt", ".sql", ".xml", ".properties", ".ini", ".md", ".template",
    ".prefs", ".classpath", ".project", ".config", ".jsp", ".gitkeep",
}
SKIP_EXT = {
    ".xlsx", ".xls", ".jpg", ".jpeg", ".png", ".gif", ".dat", ".ser", ".jar",
    ".class", ".zip", ".pdf", ".exe", ".dll",
}
SKIP_FILES = {"README.md"}


def has_cjk(text: str) -> bool:
    return bool(re.search(r"[\u4e00-\u9fff]", text))


def decode_gbk(raw: bytes) -> str:
    for enc in ("gb18030", "gbk", "gb2312"):
        try:
            return raw.decode(enc)
        except UnicodeDecodeError:
            continue
    return raw.decode("gb18030", errors="replace")


def convert_file(path: Path) -> str:
    raw = path.read_bytes()
    if not raw:
        return "empty"

    if raw.startswith(b"\xef\xbb\xbf"):
        text = raw[3:].decode("utf-8")
        path.write_text(text, encoding="utf-8", newline="")
        return "strip-bom"

    try:
        text_utf8 = raw.decode("utf-8")
        if "\ufffd" not in text_utf8:
            if has_cjk(text_utf8):
                path.write_text(text_utf8, encoding="utf-8", newline="")
                return "already-utf8"
            if all(ord(c) < 128 for c in text_utf8):
                return "ascii-utf8"
    except UnicodeDecodeError:
        pass

    text = decode_gbk(raw)
    path.write_text(text, encoding="utf-8", newline="")
    return "gbk->utf8"


def main() -> int:
    converted = 0
    skipped = 0
    for path in sorted(ROOT.rglob("*")):
        if not path.is_file():
            continue
        if path.suffix.lower() in SKIP_EXT:
            continue
        if path.suffix.lower() not in TEXT_EXT and path.suffix != "":
            continue
        if path.name in SKIP_FILES:
            skipped += 1
            continue

        result = convert_file(path)
        if result == "gbk->utf8" or result == "strip-bom":
            converted += 1
            print(f"converted: {path.relative_to(ROOT)}")
        else:
            skipped += 1

    print(f"done. converted={converted}, skipped={skipped}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
