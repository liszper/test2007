import { http, createConfig } from 'wagmi';
import { base, mainnet } from 'wagmi/chains';
import { injected, metaMask, safe, walletConnect } from 'wagmi/connectors';
// import { createGuildClient, createSigner } from '@guildxyz/sdk'
// import { UserProfile } from '@guildxyz/types'

// const guildClient = createGuildClient("My project");

const projectId = 'a1f553a67e9967aba78bc770c739bd61';
export const Config = createConfig({
  chains: [mainnet, base],
  connectors: [injected(), walletConnect({
    projectId
  }), metaMask(), safe()],
  transports: {
    [mainnet.id]: http(),
    [base.id]: http()
  }
});