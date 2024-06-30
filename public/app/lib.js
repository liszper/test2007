import * as i0 from "ws";
import * as i1 from "buffer";
import * as i2 from "react-error-boundary";
import * as i3 from "@mantine/core";
import * as i4 from "wagmi";
import * as i5 from "@guildxyz/sdk";
import * as i6 from "ecctrl";
import * as i7 from "react-dom/client";
import * as i8 from "@tanstack/react-query";
import * as i9 from "@react-three/rapier";
import * as i10 from "prando";
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

ALL["@mantine/core"] = i3;

ALL["wagmi"] = i4;

ALL["@guildxyz/sdk"] = i5;

ALL["ecctrl"] = i6;

ALL["react-dom/client"] = i7;

ALL["@tanstack/react-query"] = i8;

ALL["@react-three/rapier"] = i9;

ALL["prando"] = i10;

ALL["three"] = i11;

ALL["wagmi/chains"] = i12;

ALL["wagmi/connectors"] = i13;

ALL["@react-three/drei"] = i14;

ALL["@mantine/hooks"] = i15;

ALL["howler"] = i16;

ALL["react"] = i17;

ALL["@react-three/fiber"] = i18;

ALL["@tabler/icons-react"] = i19;
