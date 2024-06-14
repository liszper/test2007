import * as i0 from "ws";
import * as i1 from "react-error-boundary";
import * as i2 from "react-dom";
import * as i3 from "@mantine/core";
import * as i4 from "wagmi";
import * as i5 from "@guildxyz/sdk";
import * as i6 from "ecctrl";
import * as i7 from "react-dom/client";
import * as i8 from "@tanstack/react-query";
import * as i9 from "@react-three/rapier";
import * as i10 from "three";
import * as i11 from "wagmi/chains";
import * as i12 from "wagmi/connectors";
import * as i13 from "@react-three/drei";
import * as i14 from "@mantine/hooks";
import * as i15 from "react";
import * as i16 from "@react-three/fiber";
import * as i17 from "@tabler/icons-react";
import * as i18 from "@airstack/airstack-react";

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

ALL["react-error-boundary"] = i1;

ALL["react-dom"] = i2;

ALL["@mantine/core"] = i3;

ALL["wagmi"] = i4;

ALL["@guildxyz/sdk"] = i5;

ALL["ecctrl"] = i6;

ALL["react-dom/client"] = i7;

ALL["@tanstack/react-query"] = i8;

ALL["@react-three/rapier"] = i9;

ALL["three"] = i10;

ALL["wagmi/chains"] = i11;

ALL["wagmi/connectors"] = i12;

ALL["@react-three/drei"] = i13;

ALL["@mantine/hooks"] = i14;

ALL["react"] = i15;

ALL["@react-three/fiber"] = i16;

ALL["@tabler/icons-react"] = i17;

ALL["@airstack/airstack-react"] = i18;
