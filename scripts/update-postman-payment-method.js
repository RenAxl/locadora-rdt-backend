#!/usr/bin/env node

const API_BASE_URL = 'https://api.getpostman.com';
const COLLECTION_NAME = process.env.POSTMAN_COLLECTION_NAME || 'Locadora RDT';
const FOLDER_NAME = process.env.POSTMAN_PAYMENT_FOLDER_NAME || 'Pagamentos';
const API_KEY = process.env.POSTMAN_API_KEY;

if (!API_KEY) {
  console.error('Defina POSTMAN_API_KEY antes de executar.');
  process.exit(1);
}

const paymentRequests = [
  {
    name: '01 - Listar formas de pagamento',
    request: bearerRequest('GET', '{{base_url}}/payment-methods?name=&page=0&linesPerPage=10&direction=ASC&orderBy=name')
  },
  {
    name: '02 - Criar forma de pagamento',
    event: [
      testScript([
        'pm.test("Forma de pagamento criada", function () {',
        '    pm.response.to.have.status(201);',
        '    const json = pm.response.json();',
        '    pm.expect(json.id).to.exist;',
        '    pm.collectionVariables.set("payment_method_id", json.id);',
        '});'
      ])
    ],
    request: bearerRequest('POST', '{{base_url}}/payment-methods', {
      header: jsonHeaders(),
      body: {
        mode: 'raw',
        raw: '{\n  "name": "Pix",\n  "fee": 0.00\n}'
      }
    })
  },
  {
    name: '03 - Buscar forma de pagamento por ID',
    request: bearerRequest('GET', '{{base_url}}/payment-methods/{{payment_method_id}}')
  },
  {
    name: '04 - Atualizar forma de pagamento',
    request: bearerRequest('PUT', '{{base_url}}/payment-methods/{{payment_method_id}}', {
      header: jsonHeaders(),
      body: {
        mode: 'raw',
        raw: '{\n  "id": {{payment_method_id}},\n  "name": "Cartao de credito",\n  "fee": 2.50\n}'
      }
    })
  },
  {
    name: '05 - Excluir forma de pagamento por ID',
    request: bearerRequest('DELETE', '{{base_url}}/payment-methods/{{payment_method_id}}')
  },
  {
    name: '06 - Excluir formas de pagamento em lote',
    request: bearerRequest('DELETE', '{{base_url}}/payment-methods/all', {
      header: jsonHeaders(),
      body: {
        mode: 'raw',
        raw: '[\n  {{payment_method_id}}\n]'
      }
    })
  }
];

main().catch((error) => {
  console.error(error.message);
  process.exit(1);
});

async function main() {
  const collection = await findCollection(COLLECTION_NAME);
  const collectionPayload = await postman(`/collections/${collection.uid || collection.id}`);
  const root = collectionPayload.collection;
  const folder = findFolder(root.item || [], FOLDER_NAME);

  if (!folder) {
    throw new Error(`Pasta "${FOLDER_NAME}" não encontrada na coleção "${COLLECTION_NAME}".`);
  }

  folder.item = paymentRequests;
  upsertVariables(root);

  await postman(`/collections/${collection.uid || collection.id}`, {
    method: 'PUT',
    body: JSON.stringify({ collection: root })
  });

  console.log(`Pasta "${FOLDER_NAME}" atualizada na coleção "${COLLECTION_NAME}".`);
}

async function findCollection(name) {
  const result = await postman('/collections');
  const collection = (result.collections || []).find((item) => item.name === name);

  if (!collection) {
    throw new Error(`Coleção "${name}" não encontrada.`);
  }

  return collection;
}

async function postman(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'X-Api-Key': API_KEY,
      'Content-Type': 'application/json',
      ...(options.headers || {})
    }
  });

  const text = await response.text();
  const body = text ? JSON.parse(text) : {};

  if (!response.ok) {
    throw new Error(`Postman API ${response.status}: ${JSON.stringify(body)}`);
  }

  return body;
}

function findFolder(items, name) {
  for (const item of items) {
    if (item.name && item.name.toLowerCase() === name.toLowerCase() && Array.isArray(item.item)) {
      return item;
    }

    if (Array.isArray(item.item)) {
      const nested = findFolder(item.item, name);

      if (nested) {
        return nested;
      }
    }
  }

  return null;
}

function bearerRequest(method, rawUrl, overrides = {}) {
  return {
    auth: {
      type: 'bearer',
      bearer: [
        {
          key: 'token',
          value: '{{access_token}}',
          type: 'string'
        }
      ]
    },
    method,
    header: overrides.header || [],
    body: overrides.body,
    url: parsePostmanUrl(rawUrl)
  };
}

function parsePostmanUrl(rawUrl) {
  const withoutBase = rawUrl.replace('{{base_url}}/', '');
  const [pathPart, queryPart] = withoutBase.split('?');

  return {
    raw: rawUrl,
    host: ['{{base_url}}'],
    path: pathPart.split('/').filter(Boolean),
    query: queryPart
      ? queryPart.split('&').map((pair) => {
          const [key, value = ''] = pair.split('=');
          return { key, value };
        })
      : undefined
  };
}

function jsonHeaders() {
  return [
    {
      key: 'Content-Type',
      value: 'application/json'
    }
  ];
}

function testScript(lines) {
  return {
    listen: 'test',
    script: {
      exec: lines,
      type: 'text/javascript'
    }
  };
}

function upsertVariables(collection) {
  const defaults = {
    base_url: 'http://localhost:8080',
    access_token: '',
    payment_method_id: '1'
  };

  collection.variable = collection.variable || [];

  Object.entries(defaults).forEach(([key, value]) => {
    const variable = collection.variable.find((item) => item.key === key);

    if (variable) {
      return;
    }

    collection.variable.push({ key, value });
  });
}
