import React from 'react';
import { Buffer } from 'buffer';
import { http, createConfig, useAccount, useSignMessage } from 'wagmi';
import { base, mainnet, optimism, polygon } from 'wagmi/chains';
import { injected, walletConnect, coinbaseWallet } from 'wagmi/connectors';
import { createGuildClient, createSigner } from '@guildxyz/sdk';
// import { UserProfile } from '@guildxyz/types'

window.Buffer = Buffer;
const guildClient = createGuildClient("My project");
export function signIntoGuild() {
  const {
    signMessageAsync
  } = useSignMessage();
  const {
    address
  } = useAccount();
  const signerFunction = createSigner.custom(message => signMessageAsync({
    message
  }), address);
  return /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("button", {
    onClick: () => signerFunction()
  }, "Sign In"));
}
const projectId = 'a1f553a67e9967aba78bc770c739bd61';
export const Config = createConfig({
  chains: [mainnet, base, optimism, polygon],
  connectors: [injected(), coinbaseWallet(), walletConnect({
    projectId
  })
  // metaMask(),
  // safe(),
  ],
  transports: {
    [mainnet.id]: http(),
    [optimism.id]: http(),
    [base.id]: http(),
    [polygon.id]: http()
  }
});