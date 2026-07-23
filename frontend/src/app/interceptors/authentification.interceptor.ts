import { HttpInterceptorFn } from "@angular/common/http";
import LocalStorageUtils from "../utils/localStorageUtils";

export const audentificationInterceptor: HttpInterceptorFn = (req, next) => {
    if (req.url.includes('login-register')) {
        return next(req);
    }

    const token: string | null = LocalStorageUtils.getItem(LocalStorageUtils.tokenKey);

    let processedRequest;
    if (token) {
        processedRequest = req.clone({
            headers: req.headers.set('Authorization', `Bearer ${token}`),
        });
    } else {
        processedRequest = req;
    }

    return next(processedRequest);

}