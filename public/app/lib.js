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
import * as i11 from "prando";
import * as i12 from "three";
import * as i13 from "wagmi/chains";
import * as i14 from "wagmi/connectors";
import * as i15 from "@react-three/drei";
import * as i16 from "@mantine/hooks";
import * as i17 from "howler";
import * as i18 from "react";
import * as i19 from "@react-three/fiber";
import * as i20 from "@tabler/icons-react";

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

ALL["prando"] = i11;

ALL["three"] = i12;

ALL["wagmi/chains"] = i13;

ALL["wagmi/connectors"] = i14;

ALL["@react-three/drei"] = i15;

ALL["@mantine/hooks"] = i16;

ALL["howler"] = i17;

ALL["react"] = i18;

ALL["@react-three/fiber"] = i19;

ALL["@tabler/icons-react"] = i20;
