import * as i0 from "buffer";
import * as i1 from "react-error-boundary";
import * as i2 from "@mantine/core";
import * as i3 from "wagmi";
import * as i4 from "@guildxyz/sdk";
import * as i5 from "ecctrl";
import * as i6 from "react-dom/client";
import * as i7 from "@tanstack/react-query";
import * as i8 from "@react-three/rapier";
import * as i9 from "prando";
import * as i10 from "three";
import * as i11 from "wagmi/chains";
import * as i12 from "wagmi/connectors";
import * as i13 from "@react-three/drei";
import * as i14 from "@mantine/hooks";
import * as i15 from "howler";
import * as i16 from "react";
import * as i17 from "@react-three/fiber";
import * as i18 from "@tabler/icons-react";

const ALL = {};

globalThis.shadow$bridge = function(name) {
  const ret = ALL[name];
  if (ret == undefined) {
    throw new Error("Dependency: " + name + " not provided by external JS!");
  } else {
    return ret;
  }
};

ALL["buffer"] = i0;

ALL["react-error-boundary"] = i1;

ALL["@mantine/core"] = i2;

ALL["wagmi"] = i3;

ALL["@guildxyz/sdk"] = i4;

ALL["ecctrl"] = i5;

ALL["react-dom/client"] = i6;

ALL["@tanstack/react-query"] = i7;

ALL["@react-three/rapier"] = i8;

ALL["prando"] = i9;

ALL["three"] = i10;

ALL["wagmi/chains"] = i11;

ALL["wagmi/connectors"] = i12;

ALL["@react-three/drei"] = i13;

ALL["@mantine/hooks"] = i14;

ALL["howler"] = i15;

ALL["react"] = i16;

ALL["@react-three/fiber"] = i17;

ALL["@tabler/icons-react"] = i18;
