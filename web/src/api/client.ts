import type {
  MakeResponse,
  CarModelResponse,
  GenerationResponse,
  BodyResponse,
  EngineResponse,
  TransmissionResponse,
  PowertrainResponse,
  VariantResponse,
  FuelType,
  TransmissionType,
} from "../types/api";

const BASE = "/api/v1";

async function fetchJson<T>(url: string): Promise<T> {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`API error: ${response.status} ${response.statusText}`);
  }
  return response.json();
}

// --- Makes ---
export function fetchMakes(): Promise<MakeResponse[]> {
  return fetchJson(`${BASE}/makes`);
}

export function fetchMakeBySlug(slug: string): Promise<MakeResponse> {
  return fetchJson(`${BASE}/makes/slug/${slug}`);
}

// --- Models ---
export function fetchModels(makeId: string): Promise<CarModelResponse[]> {
  return fetchJson(`${BASE}/models?makeId=${makeId}`);
}

export function fetchModelById(id: string): Promise<CarModelResponse> {
  return fetchJson(`${BASE}/models/${id}`);
}

// --- Generations ---
export function fetchGenerations(modelId: string): Promise<GenerationResponse[]> {
  return fetchJson(`${BASE}/generations?modelId=${modelId}`);
}

// --- Bodies ---
export function fetchBodies(generationId: string): Promise<BodyResponse[]> {
  return fetchJson(`${BASE}/bodies?generationId=${generationId}`);
}

// --- Engines ---
export function fetchEngines(fuelType?: FuelType): Promise<EngineResponse[]> {
  const params = fuelType ? `?fuelType=${fuelType}` : "";
  return fetchJson(`${BASE}/engines${params}`);
}

export function fetchEngineById(id: string): Promise<EngineResponse> {
  return fetchJson(`${BASE}/engines/${id}`);
}

// --- Transmissions ---
export function fetchTransmissions(type?: TransmissionType): Promise<TransmissionResponse[]> {
  const params = type ? `?type=${type}` : "";
  return fetchJson(`${BASE}/transmissions${params}`);
}

export function fetchTransmissionById(id: string): Promise<TransmissionResponse> {
  return fetchJson(`${BASE}/transmissions/${id}`);
}

// --- Powertrains ---
export function fetchPowertrainById(id: string): Promise<PowertrainResponse> {
  return fetchJson(`${BASE}/powertrains/${id}`);
}

// --- Variants ---
export function fetchVariants(bodyId: string): Promise<VariantResponse[]> {
  return fetchJson(`${BASE}/variants?bodyId=${bodyId}`);
}
