"""A small reader and writer for Minecraft's NBT format (gzipped, big-endian), enough for structure files.

Values are plain Python values wrapped in tag classes where the type matters:
Byte, Short, Int, Long, Float, Double, String (str), List, Compound (dict), IntArray, ...
"""
import gzip
import io
import struct

END, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, LIST, COMPOUND, INT_ARRAY, LONG_ARRAY = range(13)


class Tag:
    type = None

    def __init__(self, value):
        self.value = value

    def __repr__(self):
        return f"{type(self).__name__}({self.value!r})"

    def __eq__(self, other):
        return type(self) is type(other) and self.value == other.value


class Byte(Tag): type = BYTE
class Short(Tag): type = SHORT
class Int(Tag): type = INT
class Long(Tag): type = LONG
class Float(Tag): type = FLOAT
class Double(Tag): type = DOUBLE
class IntArray(Tag): type = INT_ARRAY


class List(Tag):
    """A list of tags of one type; `item_type` is needed for empty lists."""
    type = LIST

    def __init__(self, value, item_type=None):
        super().__init__(list(value))
        self.item_type = item_type if item_type is not None else (_type_of(value[0]) if value else END)


def _type_of(value):
    if isinstance(value, Tag):
        return value.type
    if isinstance(value, str):
        return STRING
    if isinstance(value, dict):
        return COMPOUND
    raise TypeError(f"no NBT type for {value!r}")


# ---- Reading --------------------------------------------------------------------------------

def _read(buf, tag_type):
    if tag_type == BYTE:
        return Byte(struct.unpack(">b", buf.read(1))[0])
    if tag_type == SHORT:
        return Short(struct.unpack(">h", buf.read(2))[0])
    if tag_type == INT:
        return Int(struct.unpack(">i", buf.read(4))[0])
    if tag_type == LONG:
        return Long(struct.unpack(">q", buf.read(8))[0])
    if tag_type == FLOAT:
        return Float(struct.unpack(">f", buf.read(4))[0])
    if tag_type == DOUBLE:
        return Double(struct.unpack(">d", buf.read(8))[0])
    if tag_type == BYTE_ARRAY:
        n = struct.unpack(">i", buf.read(4))[0]
        return buf.read(n)
    if tag_type == STRING:
        n = struct.unpack(">H", buf.read(2))[0]
        return buf.read(n).decode("utf-8")
    if tag_type == LIST:
        item_type = buf.read(1)[0]
        n = struct.unpack(">i", buf.read(4))[0]
        return List([_read(buf, item_type) for _ in range(n)], item_type)
    if tag_type == COMPOUND:
        out = {}
        while True:
            t = buf.read(1)[0]
            if t == END:
                return out
            name = _read(buf, STRING)
            out[name] = _read(buf, t)
    if tag_type == INT_ARRAY:
        n = struct.unpack(">i", buf.read(4))[0]
        return IntArray(list(struct.unpack(f">{n}i", buf.read(4 * n))))
    if tag_type == LONG_ARRAY:
        n = struct.unpack(">i", buf.read(4))[0]
        return list(struct.unpack(f">{n}q", buf.read(8 * n)))
    raise ValueError(f"unknown tag type {tag_type}")


def load(data):
    """Parses gzipped NBT bytes; returns the root compound."""
    buf = io.BytesIO(gzip.decompress(data))
    t = buf.read(1)[0]
    _read(buf, STRING)  # root name
    return _read(buf, t)


# ---- Writing --------------------------------------------------------------------------------

def _write(out, value):
    if isinstance(value, Byte):
        out.write(struct.pack(">b", value.value))
    elif isinstance(value, Short):
        out.write(struct.pack(">h", value.value))
    elif isinstance(value, Int):
        out.write(struct.pack(">i", value.value))
    elif isinstance(value, Long):
        out.write(struct.pack(">q", value.value))
    elif isinstance(value, Float):
        out.write(struct.pack(">f", value.value))
    elif isinstance(value, Double):
        out.write(struct.pack(">d", value.value))
    elif isinstance(value, str):
        data = value.encode("utf-8")
        out.write(struct.pack(">H", len(data)) + data)
    elif isinstance(value, List):
        out.write(bytes([value.item_type]) + struct.pack(">i", len(value.value)))
        for item in value.value:
            _write(out, item)
    elif isinstance(value, dict):
        for name, item in value.items():
            out.write(bytes([_type_of(item)]))
            _write(out, name)
            _write(out, item)
        out.write(bytes([END]))
    elif isinstance(value, IntArray):
        out.write(struct.pack(">i", len(value.value)) + struct.pack(f">{len(value.value)}i", *value.value))
    else:
        raise TypeError(f"cannot write {value!r}")


def dump(root):
    """Gzipped NBT bytes for a root compound. The gzip header has no time stamp, so output is repeatable."""
    out = io.BytesIO()
    out.write(bytes([COMPOUND]))
    _write(out, "")
    _write(out, root)
    return gzip.compress(out.getvalue(), mtime=0)
