import * as i0 from "react-error-boundary";
import * as i1 from "react-dom";
import * as i2 from "@mantine/core";
import * as i3 from "wagmi";
import * as i4 from "ecctrl";
import * as i5 from "@tanstack/react-query";
import * as i6 from "@react-three/rapier";
import * as i7 from "three";
import * as i8 from "wagmi/chains";
import * as i9 from "wagmi/connectors";
import * as i10 from "@react-three/drei";
import * as i11 from "@mantine/hooks";
import * as i12 from "react";
import * as i13 from "@react-three/fiber";
import * as i14 from "@tabler/icons-react";
import * as i15 from "@airstack/airstack-react";

const ALL = {};

globalThis.shadow$bridge = function(name) {
  const ret = ALL[name];
  if (ret == undefined) {
    throw new Error("Dependency: " + name + " not provided by external JS!");
  } else {
    return ret;
  }
};

ALL["react-error-boundary"] = i0;

ALL["react-dom"] = i1;

ALL["@mantine/core"] = i2;

ALL["wagmi"] = i3;

ALL["ecctrl"] = i4;

ALL["@tanstack/react-query"] = i5;

ALL["@react-three/rapier"] = i6;

ALL["three"] = i7;

ALL["wagmi/chains"] = i8;

ALL["wagmi/connectors"] = i9;

ALL["@react-three/drei"] = i10;

ALL["@mantine/hooks"] = i11;

ALL["react"] = i12;

ALL["@react-three/fiber"] = i13;

ALL["@tabler/icons-react"] = i14;

ALL["@airstack/airstack-react"] = i15;
