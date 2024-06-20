import * as i0 from "ws";
import * as i1 from "buffer";
import * as i2 from "react-error-boundary";
import * as i3 from "react-dom";
import * as i4 from "@mantine/core";
import * as i5 from "wagmi";
import * as i6 from "@guildxyz/sdk";
import * as i7 from "ecctrl";
import * as i8 from "react-dom/client";
import * as i9 from "@tanstack/react-query";
import * as i10 from "@react-three/rapier";
import * as i11 from "three";
import * as i12 from "wagmi/chains";
import * as i13 from "wagmi/connectors";
import * as i14 from "@react-three/drei";
import * as i15 from "@mantine/hooks";
import * as i16 from "howler";
import * as i17 from "react";
import * as i18 from "@react-three/fiber";
import * as i19 from "@tabler/icons-react";

const ALL = {};

globalThis.shadow$bridge = function(name) {
  const ret = ALL[name];
  if (ret == undefined) {
    throw new Error("Dependency: " + name + " not provided by external JS!");
  } else {
    return ret;
  }
};

ALL["ws"] = i0;

ALL["buffer"] = i1;

ALL["react-error-boundary"] = i2;

ALL["react-dom"] = i3;

ALL["@mantine/core"] = i4;

ALL["wagmi"] = i5;

ALL["@guildxyz/sdk"] = i6;

ALL["ecctrl"] = i7;

ALL["react-dom/client"] = i8;

ALL["@tanstack/react-query"] = i9;

ALL["@react-three/rapier"] = i10;

ALL["three"] = i11;

ALL["wagmi/chains"] = i12;

ALL["wagmi/connectors"] = i13;

ALL["@react-three/drei"] = i14;

ALL["@mantine/hooks"] = i15;

ALL["howler"] = i16;

ALL["react"] = i17;

ALL["@react-three/fiber"] = i18;

ALL["@tabler/icons-react"] = i19;
