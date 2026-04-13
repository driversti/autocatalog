// --- Makes ---
export interface MakeResponse {
  id: string;
  name: string;
  country: string;
  slug: string;
}

// --- Models ---
export interface RelationResponse {
  targetModelId: string;
  type: string;
  note: string;
}

export interface CarModelResponse {
  id: string;
  makeId: string;
  name: string;
  slug: string;
  relations: RelationResponse[];
}

// --- Generations ---
export interface GenerationResponse {
  id: string;
  modelId: string;
  name: string;
  yearFrom: number;
  yearTo: number | null;
}

// --- Bodies ---
export type BodyStyle =
  | "SEDAN"
  | "HATCHBACK"
  | "LIFTBACK"
  | "WAGON"
  | "COUPE"
  | "CONVERTIBLE"
  | "SUV"
  | "CROSSOVER"
  | "MINIVAN"
  | "PICKUP"
  | "VAN";

export interface BodyResponse {
  id: string;
  generationId: string;
  bodyStyle: BodyStyle;
  lengthMm: number;
  widthMm: number;
  heightMm: number;
  wheelbaseMm: number;
  trunkVolumeLitres: number | null;
  groundClearanceMm: number | null;
}

// --- Engines ---
export type FuelType =
  | "PETROL"
  | "DIESEL"
  | "HYBRID"
  | "PLUG_IN_HYBRID"
  | "ELECTRIC"
  | "HYDROGEN"
  | "LPG";

export interface EngineResponse {
  id: string;
  code: string;
  name: string;
  fuelType: FuelType;
  displacementCc: number | null;
  powerKw: number;
  powerHp: number;
  torqueNm: number | null;
  cylinderCount: number | null;
  configuration: string | null;
  systemPowerKw: number | null;
}

// --- Transmissions ---
export type TransmissionType = "MANUAL" | "AUTOMATIC" | "DCT" | "CVT" | "SINGLE_SPEED";

export interface TransmissionResponse {
  id: string;
  type: TransmissionType;
  gearCount: number;
}

// --- Powertrains ---
export type PowertrainType = "ICE" | "ELECTRIC" | "HYBRID" | "MHEV" | "PHEV";
export type EngineRole = "PRIMARY" | "SECONDARY_ELECTRIC";

export interface PowertrainEngineResponse {
  engineId: string;
  role: EngineRole;
}

export interface PowertrainResponse {
  id: string;
  name: string;
  type: PowertrainType;
  totalPowerKw: number | null;
  totalTorqueNm: number | null;
  engines: PowertrainEngineResponse[];
}

// --- Variants ---
export type Drivetrain = "FWD" | "RWD" | "AWD" | "FOUR_WD";

export interface VariantResponse {
  id: string;
  bodyId: string;
  powertrainId: string;
  transmissionId: string;
  drivetrain: Drivetrain;
  groundClearanceMm: number | null;
  curbWeightKg: number;
  markets: string[];
}
