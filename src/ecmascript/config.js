import { Buffer } from 'buffer';
import { http, createConfig } from 'wagmi';
import { base, mainnet, optimism, polygon } from 'wagmi/chains';
import { injected, walletConnect, coinbaseWallet } from 'wagmi/connectors';
// import { UserProfile } from '@guildxyz/types'

window.Buffer = Buffer;
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