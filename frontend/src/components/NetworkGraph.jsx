import React, { useEffect, useRef, useState } from 'react';
import ForceGraph2D from 'react-force-graph-2d';
import './NetworkGraph.css';

const NetworkGraph = ({ data, width = 800, height = 600 }) => {
  const fgRef = useRef();
  const [graphData, setGraphData] = useState({ nodes: [], links: [] });
  const [highlightNodes, setHighlightNodes] = useState(new Set());
  const [highlightLinks, setHighlightLinks] = useState(new Set());

  useEffect(() => {
    if (!data) return;

    const nodes = [];
    const links = [];
    const nodeMap = new Map();

    // Procesar datos dependiendo de su estructura
    if (data.connectedWallets) {
      // Análisis de Red
      data.connectedWallets.forEach((wallet, idx) => {
        const nodeId = wallet.address || wallet.wallet || `wallet-${idx}`;
        if (!nodeMap.has(nodeId)) {
          nodeMap.set(nodeId, {
            id: nodeId,
            name: nodeId.substring(0, 8) + '...',
            val: (wallet.degree || wallet.transactionCount || 1) * 2,
            color: wallet.degree > 5 ? '#ff6b6b' : '#4ecdc4',
            ...wallet
          });
        }
      });
    } else if (data.chains) {
      // Peel Chains
      data.chains.forEach((chain, idx) => {
        const sourceId = chain.sourceWallet || `source-${idx}`;
        const targetId = chain.mainRecipient || chain.changeAddress || `target-${idx}`;

        if (!nodeMap.has(sourceId)) {
          nodeMap.set(sourceId, {
            id: sourceId,
            name: sourceId.substring(0, 8) + '...',
            val: 3,
            color: '#ff6b6b'
          });
        }

        if (!nodeMap.has(targetId)) {
          nodeMap.set(targetId, {
            id: targetId,
            name: targetId.substring(0, 8) + '...',
            val: 2,
            color: '#4ecdc4'
          });
        }

        links.push({
          source: sourceId,
          target: targetId,
          value: chain.totalAmount || 1,
          label: `${(chain.totalAmount || 0).toLocaleString()} BTC`
        });
      });
    } else if (data.path) {
      // Caminos de grafos (Dijkstra, etc)
      data.path.forEach((wallet, idx) => {
        const nodeId = wallet.address || wallet;
        if (!nodeMap.has(nodeId)) {
          nodeMap.set(nodeId, {
            id: nodeId,
            name: nodeId.substring(0, 8) + '...',
            val: idx === 0 || idx === data.path.length - 1 ? 5 : 3,
            color: idx === 0 ? '#51cf66' : idx === data.path.length - 1 ? '#ff6b6b' : '#4ecdc4'
          });
        }

        if (idx < data.path.length - 1) {
          const nextNodeId = data.path[idx + 1].address || data.path[idx + 1];
          links.push({
            source: nodeId,
            target: nextNodeId,
            value: 1
          });
        }
      });
    } else if (Array.isArray(data)) {
      // Array genérico de items
      data.forEach((item, idx) => {
        const nodeId = item.wallet || item.address || `node-${idx}`;
        if (!nodeMap.has(nodeId)) {
          nodeMap.set(nodeId, {
            id: nodeId,
            name: nodeId.substring(0, 8) + '...',
            val: (item.transactionCount || item.degree || 2) * 2,
            color: '#4ecdc4',
            ...item
          });
        }
      });
    }

    nodes.push(...nodeMap.values());
    setGraphData({ nodes, links });
  }, [data]);

  const handleNodeHover = (node) => {
    const newHighlightNodes = new Set();
    const newHighlightLinks = new Set();

    if (node) {
      newHighlightNodes.add(node);
      graphData.links.forEach(link => {
        if (link.source.id === node.id || link.target.id === node.id) {
          newHighlightLinks.add(link);
          newHighlightNodes.add(link.source);
          newHighlightNodes.add(link.target);
        }
      });
    }

    setHighlightNodes(newHighlightNodes);
    setHighlightLinks(newHighlightLinks);
  };

  return (
    <div className="network-graph-container">
      <div className="graph-legend">
        <div className="legend-item">
          <span className="legend-dot" style={{ backgroundColor: '#51cf66' }}></span>
          <span>Origen</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot" style={{ backgroundColor: '#ff6b6b' }}></span>
          <span>Alta Actividad</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot" style={{ backgroundColor: '#4ecdc4' }}></span>
          <span>Normal</span>
        </div>
      </div>

      {graphData.nodes.length > 0 ? (
        <ForceGraph2D
          ref={fgRef}
          graphData={graphData}
          width={width}
          height={height}
          nodeLabel={(node) => `
            <div style="background: rgba(0,0,0,0.8); color: white; padding: 8px; border-radius: 4px;">
              <strong>${node.name}</strong><br/>
              ${node.transactionCount ? `Transacciones: ${node.transactionCount}<br/>` : ''}
              ${node.totalAmount ? `Monto: ${node.totalAmount.toLocaleString()}<br/>` : ''}
              ${node.degree ? `Grado: ${node.degree}` : ''}
            </div>
          `}
          nodeCanvasObject={(node, ctx, globalScale) => {
            const label = node.name;
            const fontSize = 12 / globalScale;
            ctx.font = `${fontSize}px Sans-Serif`;

            const isHighlight = highlightNodes.has(node);

            // Dibujar nodo
            ctx.beginPath();
            ctx.arc(node.x, node.y, node.val, 0, 2 * Math.PI, false);
            ctx.fillStyle = isHighlight ? node.color : `${node.color}88`;
            ctx.fill();

            if (isHighlight) {
              ctx.strokeStyle = '#fff';
              ctx.lineWidth = 2 / globalScale;
              ctx.stroke();
            }

            // Dibujar etiqueta
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillStyle = isHighlight ? '#fff' : '#aaa';
            ctx.fillText(label, node.x, node.y + node.val + fontSize);
          }}
          linkWidth={link => highlightLinks.has(link) ? 3 : 1}
          linkColor={link => highlightLinks.has(link) ? '#fff' : '#999'}
          linkDirectionalParticles={link => highlightLinks.has(link) ? 4 : 0}
          linkDirectionalParticleWidth={3}
          onNodeHover={handleNodeHover}
          cooldownTicks={100}
          onEngineStop={() => fgRef.current?.zoomToFit(400)}
        />
      ) : (
        <div className="no-graph-data">
          <p>No hay datos para visualizar</p>
        </div>
      )}
    </div>
  );
};

export default NetworkGraph;

