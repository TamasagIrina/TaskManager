import { decodeJwtPayload } from "./jwtUtils";


export default class LocalStorageUtils {

    static readonly tokenKey: string = "TASKS_TOKEN";

    static setItem(key: string, value: any): void {
        localStorage.setItem(key, value);
    }

    static getItem(key: string): string | null {
        return localStorage.getItem(key);

    }

    static deleteItem(key: string): void {
        localStorage.removeItem(key);
    }

    static getEmailFromToken(): string | null {
        const token = this.getItem(this.tokenKey);
        if (!token) return null;

        const claims = decodeJwtPayload<{ email: string; exp: number; iat: number }>(token);
        return claims?.email ?? null;
    }

}