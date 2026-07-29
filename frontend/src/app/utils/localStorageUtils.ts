import { inject } from "@angular/core";
import { decodeJwtPayload } from "./jwtUtils";
import { AppComponent } from "../app.component";


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

        const claims = decodeJwtPayload<{sub:string; email: string; role: string; exp: number; iat: number }>(token);
        return claims?.email ?? null;
    }

    static getRoleFromToken(): string | null {
        const token = this.getItem(this.tokenKey);
        if (!token) return null;

        const claims = decodeJwtPayload<{ sub:string; email: string; role: string; exp: number; iat: number }>(token);
        return claims?.role ?? null;
    }

     static getIDFromToken(): string | null {
        const token = this.getItem(this.tokenKey);
        if (!token) return null;

        const claims = decodeJwtPayload<{ sub:string; email: string; role: string; exp: number; iat: number }>(token);
        return claims?.sub ?? null;
    }


}