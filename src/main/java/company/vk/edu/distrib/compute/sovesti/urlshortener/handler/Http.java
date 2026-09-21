package company.vk.edu.distrib.compute.sovesti.urlshortener.handler;

public interface Http {

    interface Method {

        String GET = "GET";
        String POST = "POST";
        String PUT = "PUT";
        String DELETE = "DELETE";

    }

    interface StatusCode {

        int OK = 200;
        int Created = 201;
        int Accepted = 202;
        int MovedPermanently = 301;
        int Unathorized = 401;
        int NotFound = 404;
        int UnprocessableContent = 422;
        int InternalError = 500;

    }

    interface Header {

        String Authenticate = "WWW-Authenticate";
        String Authorization = "Authorization";
        String ContentType = "Content-Type";
        String Host = "Host";
        String Location = "Location";

    }

    interface Authentication {

        String Basic = "Basic";
        String Realm = "realm";

    }

    interface ContentType {

        String HtmlUtf8 = "text/html; charset=utf-8";

    }
}
